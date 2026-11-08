package com.dextra.drill;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Synthetic hands: palm width 0.1 of the frame, the thumb at (0.40, 0.50), the other fingers spread unless a test moves them. */
class PinchDetectorTest {

    private static final double ASPECT = 0.75;
    private static final double PALM = 0.10;
    private static final long FRAME_MS = 33;

    /** Tips with the thumb-to-`partner` gap in palm widths; `follower` (or -1) trails the thumb at `followGap`. */
    private static double[] hand(int partner, double gap, int follower, double followGap) {
        double[] t = {0.40, 0.50, 0.70, 0.30, 0.60, 0.20, 0.70, 0.24, 0.80, 0.30};
        // partner moves along x away from the thumb: the distance in palm widths is exactly `gap`
        t[2 * partner] = 0.40 + gap * PALM;
        t[2 * partner + 1] = 0.50;
        if (follower >= 0) {
            // follower sits above the thumb: y distance scaled by ASPECT gives `followGap` palm widths
            t[2 * follower] = 0.40;
            t[2 * follower + 1] = 0.50 - followGap * PALM / ASPECT;
        }
        return t;
    }

    /** Feeds `pinches` pinches (open 6 frames, closed 6 frames, open again) with `pauseMs` of open hand between them. */
    private static Optional<int[]> pinch(PinchDetector d, long[] clock, int partner, int pinches, long pauseMs,
                                         int follower, double followClosed) {
        Optional<int[]> result = Optional.empty();
        for (int p = 0; p < pinches; p++) {
            for (long t = 0; t < pauseMs; t += FRAME_MS) {
                result = or(result, d.onFrame(clock[0] += FRAME_MS, hand(partner, 1.0, follower, 1.2), PALM));
            }
            for (int f = 0; f < 6; f++) {
                result = or(result, d.onFrame(clock[0] += FRAME_MS, hand(partner, 1.0, follower, 1.2), PALM));
            }
            for (int f = 0; f < 6; f++) {
                result = or(result, d.onFrame(clock[0] += FRAME_MS, hand(partner, 0.15, follower, followClosed), PALM));
            }
        }
        for (int f = 0; f < 6; f++) {
            result = or(result, d.onFrame(clock[0] += FRAME_MS, hand(partner, 1.0, follower, 1.2), PALM));
        }
        return result;
    }

    private static Optional<int[]> or(Optional<int[]> a, Optional<int[]> b) {
        return a.isPresent() ? a : b;
    }

    @Test
    void threePinchesLockThumbAndIndex() {
        Optional<int[]> r = pinch(new PinchDetector(ASPECT), new long[]{0}, 1, 3, 0, -1, 0);
        assertTrue(r.isPresent());
        assertArrayEquals(new int[]{0, 1}, r.get());
    }

    @Test
    void twoPinchesDoNotLockButShowProgress() {
        PinchDetector d = new PinchDetector(ASPECT);
        assertFalse(pinch(d, new long[]{0}, 1, 2, 0, -1, 0).isPresent());
        assertEquals(new PinchDetector.Progress(0, 1, 2), d.progress());
    }

    @Test
    void pinchesFurtherApartThanTheWindowDoNotLock() {
        assertFalse(pinch(new PinchDetector(ASPECT), new long[]{0}, 1, 3, 3000, -1, 0).isPresent());
    }

    @Test
    void shakyFingersNearTheThresholdNeverCount() {
        PinchDetector d = new PinchDetector(ASPECT);
        long t = 0;
        for (int i = 0; i < 300; i++) {
            // hovers just above CLOSE, then dips under CLOSE but never spreads past OPEN
            double gap = i < 150 ? 0.40 + 0.15 * Math.sin(i * 0.7) : 0.45 + 0.12 * Math.sin(i * 0.7);
            assertFalse(d.onFrame(t += FRAME_MS, hand(1, gap, -1, 0), PALM).isPresent(), "frame " + i);
        }
        assertNull(d.progress());
    }

    @Test
    void whenAnotherFingerFollowsThePinchTheClosestPairWins() {
        // the middle finger curls toward the thumb with every pinch (0.30 palm widths), the index touches it (0.15)
        Optional<int[]> r = pinch(new PinchDetector(ASPECT), new long[]{0}, 1, 3, 0, 2, 0.30);
        assertTrue(r.isPresent());
        assertArrayEquals(new int[]{0, 1}, r.get());
    }

    @Test
    void anyTwoFingersWorkForSomeoneWithoutAnIndexFinger() {
        Optional<int[]> r = pinch(new PinchDetector(ASPECT), new long[]{0}, 2, 3, 0, -1, 0);
        assertTrue(r.isPresent());
        assertArrayEquals(new int[]{0, 2}, r.get());
    }

    @Test
    void framesWithoutFingertipsOrPalmAreIgnored() {
        PinchDetector d = new PinchDetector(ASPECT);
        assertFalse(d.onFrame(10, null, PALM).isPresent());
        assertFalse(d.onFrame(20, hand(1, 0.1, -1, 0), 0).isPresent());
        assertNull(d.progress());
    }
}
