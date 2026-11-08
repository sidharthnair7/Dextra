package com.dextra.drill;

import com.dextra.drill.DrillTypes.Hit;
import com.dextra.drill.DrillTypes.Metric;
import com.dextra.drill.DrillTypes.Point;
import com.dextra.drill.DrillTypes.Refusal;
import com.dextra.drill.DrillTypes.ScoreReport;
import com.dextra.drill.DrillTypes.Target;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Scores one attempt from measured points only. Plain code, no model: every number comes with its formula.
 * Distances are in screen widths (y is scaled by the 4:3 aspect so x and y use the same unit).
 * The scored moves are target 1 to 2, 2 to 3, 3 to 4 and 4 to 5; reaching target 1 is the warm-up.
 */
public final class Scorer {

    public static final double ASPECT = 480.0 / 640.0;        // height / width of the camera frame
    public static final double MIN_COVERAGE = 0.80;           // below this, the attempt isn't scored at all
    public static final double ZERO_ACCURACY_DEVIATION = 0.10; // mean distance off the path that scores 0
    public static final double JUMP_LIMIT = 0.15;             // a one-frame move this big is a tracking glitch
    public static final int MIN_LOST_FRAMES = 3;              // shorter dropouts aren't listed as refusals

    private Scorer() {
    }

    public static ScoreReport score(List<Point> points, List<Target> targets, List<Hit> hits) {
        List<Refusal> refusals = new ArrayList<>();
        boolean[] trusted = trustMask(points, refusals);

        long okFrames = points.stream().filter(Point::ok).count();
        double coverage = points.isEmpty() ? 0 : (double) okFrames / points.size();
        Metric coverageMetric = new Metric(round1(coverage * 100), "%",
                "frames with a confident fingertip / all frames in the attempt",
                okFrames + " of " + points.size() + " frames");

        Map<String, Metric> metrics = new LinkedHashMap<>();
        if (hits.size() < targets.size()) {
            metrics.put("coverage", coverageMetric);
            return new ScoreReport(false, "course not finished (" + hits.size() + " of " + targets.size() + " targets)", nullMetrics(metrics), refusals);
        }
        if (coverage < MIN_COVERAGE) {
            metrics.put("coverage", coverageMetric);
            return new ScoreReport(false, String.format(Locale.ROOT,
                    "not enough reliable tracking to score: %.0f%% of frames, needs %.0f%%", coverage * 100, MIN_COVERAGE * 100),
                    nullMetrics(metrics), refusals);
        }

        double devSum = 0;
        double ratioSum = 0;
        double smoothSum = 0;
        int moves = 0;
        int peaksTotal = 0;
        StringBuilder peakDetail = new StringBuilder();
        for (int k = 0; k + 1 < hits.size(); k++) {
            Target a = byId(targets, hits.get(k).targetId());
            Target b = byId(targets, hits.get(k + 1).targetId());
            long from = leftTargetAt(points, trusted, a, hits.get(k).t(), hits.get(k + 1).t());
            List<double[]> seg = segmentPoints(points, trusted, from, hits.get(k + 1).t());
            double ax = a.x();
            double ay = a.y() * ASPECT;
            double bx = b.x();
            double by = b.y() * ASPECT;
            devSum += meanDeviation(seg, ax, ay, bx, by);
            double straight = Math.hypot(bx - ax, by - ay);
            ratioSum += straight == 0 ? 1 : pathLength(seg) / straight;
            int peaks = speedPeaks(seg);
            peaksTotal += peaks;
            smoothSum += 100.0 / peaks;
            peakDetail.append(k == 0 ? "" : ", ").append(peaks);
            moves++;
        }
        double meanDev = devSum / moves;
        double accuracy = clamp(100 * (1 - meanDev / ZERO_ACCURACY_DEVIATION));
        double moveSeconds = (hits.get(hits.size() - 1).t() - hits.get(0).t()) / 1000.0 / moves;

        metrics.put("accuracy", new Metric(round0(accuracy), "score",
                "100 x (1 - mean distance from the straight path / 0.10 screen widths)",
                String.format(Locale.ROOT, "mean distance off the path %.3f screen widths; path %.2fx the straight line",
                        meanDev, ratioSum / moves)));
        metrics.put("smoothness", new Metric(round0(smoothSum / moves), "score",
                "average of 100 / (speed peaks per move): one smooth move = 100, each extra correction lowers it",
                "speed peaks per move: " + peakDetail + " (" + peaksTotal + " total over " + moves + " moves)"));
        metrics.put("speed", new Metric(round1(moveSeconds), "s per target",
                "time from target 1 to the last target / number of moves",
                String.format(Locale.ROOT, "%d moves in %.1f s", moves, moveSeconds * moves)));
        metrics.put("coverage", coverageMetric);
        return new ScoreReport(true, null, metrics, refusals);
    }

    /**
     * A move starts when the finger leaves the target it just hit: the last trusted frame still inside
     * that target's radius. So the slow-down into a target is never counted as part of the next move.
     */
    static long leftTargetAt(List<Point> points, boolean[] trusted, Target a, long fromT, long toT) {
        long left = fromT;
        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            if (trusted[i] && p.t() >= fromT && p.t() <= toT
                    && Math.hypot(p.x() - a.x(), (p.y() - a.y()) * ASPECT) <= a.r()) {
                left = p.t();
            }
        }
        return left;
    }

    /** Marks frames that can be scored; lists lost stretches and glitch jumps as refusals. */
    static boolean[] trustMask(List<Point> points, List<Refusal> refusals) {
        boolean[] trusted = new boolean[points.size()];
        int lostStart = -1;
        Point prevOk = null;
        Point rejected = null;
        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            if (!p.ok()) {
                if (lostStart < 0) {
                    lostStart = i;
                }
                continue;
            }
            if (lostStart >= 0) {
                if (i - lostStart >= MIN_LOST_FRAMES) {
                    refusals.add(new Refusal(points.get(lostStart).t(), p.t(), "hand lost or tracker not sure"));
                }
                lostStart = -1;
                prevOk = null; // the hand moved while unseen: no jump check across a gap
                rejected = null;
            }
            if (prevOk != null && distance(prevOk, p) > JUMP_LIMIT) {
                if (rejected != null && distance(rejected, p) <= JUMP_LIMIT) {
                    // two frames in a row agree: the finger really moved fast; trust it from here
                    trusted[i] = true;
                    prevOk = p;
                    rejected = null;
                    continue;
                }
                refusals.add(new Refusal(prevOk.t(), p.t(), "impossible jump, treated as a tracking glitch"));
                rejected = p;
                continue;
            }
            trusted[i] = true;
            prevOk = p;
            rejected = null;
        }
        if (lostStart >= 0 && points.size() - lostStart >= MIN_LOST_FRAMES) {
            refusals.add(new Refusal(points.get(lostStart).t(), points.get(points.size() - 1).t(), "hand lost or tracker not sure"));
        }
        return trusted;
    }

    /** Trusted points with fromT <= t <= toT, as [t (s), x, y (width units)]. */
    static List<double[]> segmentPoints(List<Point> points, boolean[] trusted, long fromT, long toT) {
        List<double[]> seg = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            if (trusted[i] && p.t() >= fromT && p.t() <= toT) {
                seg.add(new double[]{p.t() / 1000.0, p.x(), p.y() * ASPECT});
            }
        }
        return seg;
    }

    static double meanDeviation(List<double[]> seg, double ax, double ay, double bx, double by) {
        if (seg.isEmpty()) {
            return 0;
        }
        double sum = 0;
        for (double[] p : seg) {
            sum += distanceToSegment(p[1], p[2], ax, ay, bx, by);
        }
        return sum / seg.size();
    }

    static double distanceToSegment(double px, double py, double ax, double ay, double bx, double by) {
        double dx = bx - ax;
        double dy = by - ay;
        double len2 = dx * dx + dy * dy;
        double u = len2 == 0 ? 0 : ((px - ax) * dx + (py - ay) * dy) / len2;
        u = Math.max(0, Math.min(1, u));
        return Math.hypot(px - (ax + u * dx), py - (ay + u * dy));
    }

    static double pathLength(List<double[]> seg) {
        double len = 0;
        for (int i = 1; i < seg.size(); i++) {
            len += Math.hypot(seg.get(i)[1] - seg.get(i - 1)[1], seg.get(i)[2] - seg.get(i - 1)[2]);
        }
        return len;
    }

    /**
     * Counts the peaks in the speed profile of one move. A smooth reach has one bell-shaped peak;
     * each correction adds one. Speeds are smoothed over 5 frames; a peak counts only if it is at least
     * 25% of the move's top speed and the speed dips below 60% of the smaller peak before it.
     */
    static int speedPeaks(List<double[]> seg) {
        int n = seg.size();
        if (n < 6) {
            return 1;
        }
        double[] v = new double[n - 1];
        for (int i = 1; i < n; i++) {
            double dt = Math.max(1e-3, seg.get(i)[0] - seg.get(i - 1)[0]);
            v[i - 1] = Math.hypot(seg.get(i)[1] - seg.get(i - 1)[1], seg.get(i)[2] - seg.get(i - 1)[2]) / dt;
        }
        double[] s = new double[v.length];
        for (int i = 0; i < v.length; i++) {
            double sum = 0;
            int c = 0;
            for (int j = Math.max(0, i - 2); j <= Math.min(v.length - 1, i + 2); j++) {
                sum += v[j];
                c++;
            }
            s[i] = sum / c;
        }
        double max = 0;
        for (double x : s) {
            max = Math.max(max, x);
        }
        if (max <= 0) {
            return 1;
        }
        int count = 0;
        int prev = -1;
        for (int i = 0; i < s.length; i++) {
            boolean leftOk = i == 0 || s[i] >= s[i - 1];
            boolean rightOk = i == s.length - 1 || s[i] > s[i + 1];
            if (!leftOk || !rightOk || s[i] < 0.25 * max) {
                continue;
            }
            if (prev < 0) {
                prev = i;
                count = 1;
                continue;
            }
            double valley = Double.MAX_VALUE;
            for (int j = prev; j <= i; j++) {
                valley = Math.min(valley, s[j]);
            }
            if (valley < 0.6 * Math.min(s[prev], s[i])) {
                count++;
                prev = i;
            } else if (s[i] > s[prev]) {
                prev = i;
            }
        }
        return Math.max(1, count);
    }

    private static Map<String, Metric> nullMetrics(Map<String, Metric> keep) {
        Map<String, Metric> out = new LinkedHashMap<>();
        out.put("accuracy", new Metric(null, "score", "not scored", null));
        out.put("smoothness", new Metric(null, "score", "not scored", null));
        out.put("speed", new Metric(null, "s per target", "not scored", null));
        out.putAll(keep);
        return out;
    }

    private static Target byId(List<Target> targets, int id) {
        for (Target t : targets) {
            if (t.id() == id) {
                return t;
            }
        }
        throw new IllegalArgumentException("unknown target " + id);
    }

    private static double distance(Point a, Point b) {
        return Math.hypot(b.x() - a.x(), (b.y() - a.y()) * ASPECT);
    }

    private static double clamp(double v) {
        return Math.max(0, Math.min(100, v));
    }

    private static double round0(double v) {
        return (double) Math.round(v);
    }

    private static double round1(double v) {
        return Math.round(v * 10) / 10.0;
    }
}
