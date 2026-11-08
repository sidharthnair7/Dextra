package com.dextra.live;

import java.util.Locale;

/**
 * One webcam frame's result.
 *
 * @param t          milliseconds since tracking started
 * @param found      false when no hand was found with enough confidence
 * @param x          fingertip x, 0 to 1 across the mirrored camera image
 * @param y          fingertip y, 0 to 1 down the image
 * @param confidence hand-model confidence, 0 to 1
 * @param fps        frames per second, smoothed
 */
public record TrackPoint(long t, boolean found, double x, double y, double confidence, double fps) {

    public String toJson() {
        if (!found) {
            return String.format(Locale.ROOT, "{\"type\":\"point\",\"t\":%d,\"ok\":false,\"fps\":%.1f}", t, fps);
        }
        return String.format(Locale.ROOT,
                "{\"type\":\"point\",\"t\":%d,\"ok\":true,\"x\":%.4f,\"y\":%.4f,\"c\":%.3f,\"fps\":%.1f}",
                t, x, y, confidence, fps);
    }
}
