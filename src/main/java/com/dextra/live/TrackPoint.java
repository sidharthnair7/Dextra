package com.dextra.live;

import java.util.Locale;

/**
 * One webcam frame's result.
 *
 * @param t          milliseconds since tracking started
 * @param found      false when no hand was found with enough confidence
 * @param x          index fingertip x, 0 to 1 across the mirrored camera image (the drill's pointer)
 * @param y          index fingertip y, 0 to 1 down the image
 * @param confidence hand-model confidence, 0 to 1
 * @param fps        frames per second, smoothed
 * @param tips       all five fingertips, thumb to pinky, as x0, y0, ..., x4, y4 in the same 0 to 1 units; null if not known
 * @param palm       palm width (index knuckle to pinky knuckle) as a share of the frame width; 0 if not known
 */
public record TrackPoint(long t, boolean found, double x, double y, double confidence, double fps, double[] tips, double palm) {

    /** A frame without the per-finger detail (no hand, or tests that only need the pointer). */
    public TrackPoint(long t, boolean found, double x, double y, double confidence, double fps) {
        this(t, found, x, y, confidence, fps, null, 0);
    }

    public String toJson() {
        if (!found) {
            return String.format(Locale.ROOT, "{\"type\":\"point\",\"t\":%d,\"ok\":false,\"fps\":%.1f}", t, fps);
        }
        String base = String.format(Locale.ROOT,
                "{\"type\":\"point\",\"t\":%d,\"ok\":true,\"x\":%.4f,\"y\":%.4f,\"c\":%.3f,\"fps\":%.1f",
                t, x, y, confidence, fps);
        if (tips == null) {
            return base + "}";
        }
        StringBuilder sb = new StringBuilder(base).append(",\"tips\":[");
        for (int k = 0; k < 5; k++) {
            if (k > 0) {
                sb.append(',');
            }
            sb.append(String.format(Locale.ROOT, "[%.4f,%.4f]", tips[2 * k], tips[2 * k + 1]));
        }
        return sb.append(String.format(Locale.ROOT, "],\"palm\":%.4f}", palm)).toString();
    }
}
