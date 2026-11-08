package com.dextra.drill;

import com.dextra.drill.DrillTypes.AttemptSummary;
import com.dextra.drill.DrillTypes.AttemptView;
import com.dextra.drill.DrillTypes.Hit;
import com.dextra.drill.DrillTypes.Point;
import com.dextra.drill.DrillTypes.ScoreReport;
import com.dextra.drill.DrillTypes.StartResponse;
import com.dextra.drill.DrillTypes.Target;
import com.dextra.live.LiveSocket;
import com.dextra.live.TrackPoint;
import com.dextra.live.TrackPointListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Runs one drill at a time: records every frame, detects target hits in order, and scores the attempt at the end. */
@Service
public class AttemptService implements TrackPointListener {

    public static final long TIMEOUT_MS = 60_000;

    private final LiveSocket socket;
    private final GeminiCoach coach;
    /** One thread, so coaching calls never hold up the tracker loop. */
    private final ExecutorService coachThread = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "dextra-coach");
        t.setDaemon(true);
        return t;
    });
    private final Map<String, Attempt> attempts = new LinkedHashMap<>();
    private Attempt running;

    public AttemptService(LiveSocket socket, GeminiCoach coach) {
        this.socket = socket;
        this.coach = coach;
    }

    /**
     * The fixed course, so attempts can be compared. Normalized camera coordinates; r = 4% of the screen width.
     * The rows stay between 25% and 65% of the frame height: the tracker needs the whole palm in view, and the
     * palm sits below the fingertip, so a lower target pushes the palm out of the picture first
     * (the first real-hand test lost the hand for 12.7 s there).
     */
    public static List<Target> course() {
        return List.of(
                new Target(1, 0.20, 0.25, 0.04),
                new Target(2, 0.80, 0.25, 0.04),
                new Target(3, 0.50, 0.45, 0.04),
                new Target(4, 0.20, 0.65, 0.04),
                new Target(5, 0.80, 0.65, 0.04));
    }

    private static final class Attempt {
        final String id;
        final Instant startedAt;
        final List<Target> targets;
        final List<Point> points = new ArrayList<>();
        final List<Hit> hits = new ArrayList<>();
        long t0 = -1;
        String state = "running";
        ScoreReport report;
        String feedback;

        Attempt(String id, Instant startedAt, List<Target> targets) {
            this.id = id;
            this.startedAt = startedAt;
            this.targets = targets;
        }
    }

    public synchronized StartResponse start() {
        if (running != null) {
            finish(running, "replaced");
        }
        Attempt a = new Attempt(UUID.randomUUID().toString().substring(0, 8), Instant.now(), course());
        attempts.put(a.id, a);
        running = a;
        return new StartResponse(a.id, a.targets);
    }

    @Override
    public synchronized void onPoint(TrackPoint p) {
        Attempt a = running;
        if (a == null) {
            return;
        }
        if (a.t0 < 0) {
            a.t0 = p.t();
        }
        long t = p.t() - a.t0;
        a.points.add(new Point(t, p.x(), p.y(), p.confidence(), p.found()));
        if (p.found() && a.hits.size() < a.targets.size()) {
            Target next = a.targets.get(a.hits.size());
            if (Math.hypot(p.x() - next.x(), (p.y() - next.y()) * Scorer.ASPECT) <= next.r()) {
                Hit h = new Hit(next.id(), t);
                a.hits.add(h);
                socket.broadcast(hitJson(a.id, h));
                if (a.hits.size() == a.targets.size()) {
                    finish(a, "done");
                    return;
                }
            }
        }
        if (t > TIMEOUT_MS) {
            finish(a, "timeout");
        }
    }

    private void finish(Attempt a, String state) {
        a.state = state;
        a.report = Scorer.score(a.points, a.targets, a.hits);
        if (running == a) {
            running = null;
        }
        socket.broadcast("{\"type\":\"attemptEnded\",\"attemptId\":\"" + a.id + "\",\"state\":\"" + state + "\"}");
        ScoreReport report = a.report;
        String id = a.id;
        coachThread.submit(() -> {
            String text = coach.coach(report, state);
            setFeedback(id, text);
            socket.broadcast(feedbackJson(id, text));
        });
    }

    static String feedbackJson(String attemptId, String text) {
        var node = tools.jackson.databind.json.JsonMapper.builder().build().createObjectNode();
        node.put("type", "feedback").put("attemptId", attemptId).put("text", text);
        return node.toString();
    }

    static String hitJson(String attemptId, Hit h) {
        return "{\"type\":\"hit\",\"attemptId\":\"" + attemptId + "\",\"targetId\":" + h.targetId() + ",\"t\":" + h.t() + "}";
    }

    public synchronized Optional<AttemptView> get(String id) {
        Attempt a = attempts.get(id);
        if (a == null) {
            return Optional.empty();
        }
        ScoreReport r = a.report;
        return Optional.of(new AttemptView(a.id, a.startedAt.toString(), a.state, a.targets, List.copyOf(a.points),
                List.copyOf(a.hits), r != null && r.scored(),
                r == null ? "attempt still running" : r.refusedReason(),
                r == null ? Map.of() : r.metrics(), r == null ? List.of() : r.refusals(), a.feedback));
    }

    /** Lets later phases (Gemini feedback) attach text to a finished attempt. */
    public synchronized void setFeedback(String id, String feedback) {
        Attempt a = attempts.get(id);
        if (a != null) {
            a.feedback = feedback;
        }
    }

    public synchronized List<AttemptSummary> list() {
        List<AttemptSummary> out = new ArrayList<>();
        for (Attempt a : attempts.values()) {
            Double acc = null;
            Double smooth = null;
            if (a.report != null && a.report.scored()) {
                acc = a.report.metrics().get("accuracy").value();
                smooth = a.report.metrics().get("smoothness").value();
            }
            out.add(0, new AttemptSummary(a.id, a.startedAt.toString(), a.state, acc, smooth));
        }
        return out;
    }
}
