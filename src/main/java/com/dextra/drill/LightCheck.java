package com.dextra.drill;

import com.dextra.live.TrackPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The check before a drill: the user holds their hand still for a few seconds and we measure
 * how often the hand is found, the frame rate, and how much the fingertip shakes on its own (tracker noise).
 * Pass or fail is decided here, in plain code.
 */
public final class LightCheck {

    public static final double MAX_JITTER_PX = 3.0;
    public static final double MIN_COVERAGE = 0.90;
    public static final double MIN_FPS = 20;
    static final double FRAME_WIDTH_PX = 640;

    private LightCheck() {
    }

    /** jitterPx is null when there were fewer than 2 frames with a hand. */
    public record Result(boolean pass, Double jitterPx, double coveragePercent, double fps, int frames, List<String> reasons) {
    }

    public static Result evaluate(List<TrackPoint> points, double seconds) {
        int frames = points.size();
        double fps = seconds > 0 ? frames / seconds : 0;
        List<TrackPoint> ok = points.stream().filter(TrackPoint::found).toList();
        double coverage = frames == 0 ? 0 : (double) ok.size() / frames;
        Double jitter = null;
        if (ok.size() >= 2) {
            double mx = ok.stream().mapToDouble(TrackPoint::x).average().orElse(0);
            double my = ok.stream().mapToDouble(TrackPoint::y).average().orElse(0);
            double var = 0;
            for (TrackPoint p : ok) {
                double dx = p.x() - mx;
                double dy = (p.y() - my) * Scorer.ASPECT;
                var += dx * dx + dy * dy;
            }
            jitter = Math.round(Math.sqrt(var / ok.size()) * FRAME_WIDTH_PX * 10) / 10.0;
        }
        List<String> reasons = new ArrayList<>();
        if (frames == 0) {
            reasons.add("no frames from the camera");
        } else {
            if (coverage < MIN_COVERAGE) {
                reasons.add(String.format(Locale.ROOT, "hand found in only %.0f%% of frames (needs %.0f%%)", coverage * 100, MIN_COVERAGE * 100));
            }
            if (fps < MIN_FPS) {
                reasons.add(String.format(Locale.ROOT, "only %.0f frames per second (needs %.0f)", fps, MIN_FPS));
            }
            if (jitter != null && jitter > MAX_JITTER_PX) {
                reasons.add(String.format(Locale.ROOT, "fingertip moves %.1f px while held still (limit %.1f)", jitter, MAX_JITTER_PX));
            }
        }
        return new Result(reasons.isEmpty(), jitter, Math.round(coverage * 1000) / 10.0, Math.round(fps * 10) / 10.0, frames, reasons);
    }
}
