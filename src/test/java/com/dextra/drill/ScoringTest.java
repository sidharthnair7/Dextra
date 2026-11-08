package com.dextra.drill;

import com.dextra.drill.DrillTypes.AttemptView;
import com.dextra.live.LiveSocket;
import com.dextra.live.TrackPoint;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The scoring must reward a clean path, punish a shaky one, and refuse what it can't measure. */
class ScoringTest {

    private static AttemptView run(SyntheticPaths paths) {
        AttemptService service = new AttemptService(new LiveSocket(), new GeminiCoach("", "gemini-2.5-flash", "gemini-flash-latest"), AttemptLog.memoryOnly(), new FingerLock());
        String id = service.start().attemptId();
        for (TrackPoint p : paths.build(AttemptService.course())) {
            service.onPoint(p);
        }
        return service.get(id).orElseThrow();
    }

    private static double metric(AttemptView v, String name) {
        return v.metrics().get(name).value();
    }

    private static void print(String label, AttemptView v) {
        System.out.printf("%-12s scored=%s acc=%s smooth=%s speed=%s cov=%s refusals=%d %s%n", label, v.scored(),
                v.metrics().get("accuracy").value(), v.metrics().get("smoothness").value(),
                v.metrics().get("speed").value(), v.metrics().get("coverage").value(), v.refusals().size(),
                v.refusedReason() == null ? "" : v.refusedReason());
    }

    @Test
    void cleanPathScoresHigh() {
        AttemptView v = run(new SyntheticPaths());
        print("clean", v);
        assertEquals("done", v.state());
        assertTrue(v.scored());
        assertEquals(5, v.hits().size());
        assertTrue(metric(v, "accuracy") >= 95);
        assertEquals(100.0, metric(v, "smoothness"));
        assertEquals(100.0, metric(v, "coverage"));
        assertTrue(v.metrics().get("accuracy").formula().contains("mean distance"));
    }

    @Test
    void trackerNoiseDoesNotFakeCorrections() {
        SyntheticPaths p = new SyntheticPaths();
        p.noise = 0.002; // about 1.3 px on a 640 px frame
        AttemptView v = run(p);
        print("noisy-clean", v);
        assertTrue(v.scored());
        assertTrue(metric(v, "accuracy") >= 90);
        assertTrue(metric(v, "smoothness") >= 87.5, "noise alone may add at most one false peak");
    }

    @Test
    void shakyPathScoresClearlyLowerThanClean() {
        AttemptView clean = run(new SyntheticPaths());
        SyntheticPaths p = new SyntheticPaths();
        p.wiggle = 0.03;   // drifts off the line
        p.submoves = 3;    // and reaches in three start-stop pieces
        AttemptView shaky = run(p);
        print("shaky", shaky);
        assertTrue(shaky.scored());
        assertTrue(metric(clean, "accuracy") - metric(shaky, "accuracy") >= 15);
        assertTrue(metric(clean, "smoothness") - metric(shaky, "smoothness") >= 15);
    }

    @Test
    void stopAndGoCountsAsTwoMoves() {
        SyntheticPaths p = new SyntheticPaths();
        p.stopAndGo = true;
        AttemptView v = run(p);
        print("stop-and-go", v);
        assertEquals(50.0, metric(v, "smoothness"));
    }

    @Test
    void lostHandIsListedAndNotScored() {
        SyntheticPaths p = new SyntheticPaths();
        p.dropoutMove = 1;
        p.dropoutFrames = 10;
        AttemptView v = run(p);
        print("dropout", v);
        assertTrue(v.scored(), "one short dropout keeps coverage above 80%");
        assertTrue(metric(v, "coverage") < 100);
        assertTrue(v.refusals().stream().anyMatch(r -> r.reason().contains("hand lost")));
        assertEquals(1, v.refusals().size(), "one dropout is one refusal, not a cascade");
    }

    @Test
    void refusesToScoreWhenTrackingIsUnreliable() {
        SyntheticPaths p = new SyntheticPaths();
        p.randomDropRate = 0.35;
        AttemptView v = run(p);
        print("unreliable", v);
        if (v.hits().size() == 5) {
            assertFalse(v.scored());
            assertTrue(v.refusedReason().contains("not enough reliable tracking"));
        } else {
            assertFalse(v.scored());
        }
        assertNull(v.metrics().get("accuracy").value());
    }

    @Test
    void glitchJumpIsIgnored() {
        SyntheticPaths p = new SyntheticPaths();
        p.glitchMove = 2;
        AttemptView v = run(p);
        print("glitch", v);
        assertTrue(v.scored());
        assertTrue(v.refusals().stream().anyMatch(r -> r.reason().contains("impossible jump")));
        assertTrue(metric(v, "accuracy") >= 95, "one glitch frame must not move the score");
    }

    @Test
    void unfinishedAndTimedOutAttempts() {
        AttemptService service = new AttemptService(new LiveSocket(), new GeminiCoach("", "gemini-2.5-flash", "gemini-flash-latest"), AttemptLog.memoryOnly(), new FingerLock());
        String id = service.start().attemptId();
        service.onPoint(new TrackPoint(0, true, 0.5, 0.5, 0.9, 30));
        assertEquals("running", service.get(id).orElseThrow().state());
        assertFalse(service.get(id).orElseThrow().scored());
        service.onPoint(new TrackPoint(AttemptService.TIMEOUT_MS + 100, true, 0.5, 0.5, 0.9, 30));
        AttemptView v = service.get(id).orElseThrow();
        assertEquals("timeout", v.state());
        assertFalse(v.scored());
        assertTrue(v.refusedReason().contains("course not finished"));
    }

    @Test
    void hitMessageMatchesContract() {
        assertEquals("{\"type\":\"hit\",\"attemptId\":\"a1b2\",\"targetId\":2,\"t\":4210}",
                AttemptService.hitJson("a1b2", new DrillTypes.Hit(2, 4210)));
    }

    @Test
    void speedPeaksOnTextbookShapes() {
        List<double[]> smooth = toSeg(SyntheticPaths.move(0.2, 0.2, 0.8, 0.2, 27));
        assertEquals(1, Scorer.speedPeaks(smooth));
    }

    private static List<double[]> toSeg(List<double[]> xy) {
        java.util.ArrayList<double[]> seg = new java.util.ArrayList<>();
        for (int i = 0; i < xy.size(); i++) {
            seg.add(new double[]{i * 0.033, xy.get(i)[0], xy.get(i)[1] * Scorer.ASPECT});
        }
        return seg;
    }
}
