package com.dextra.drill;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

/**
 * Finds the two fingers someone pinches together three times. Pure logic, fed one frame at a time, so it's testable
 * without a camera. Distances are in palm widths (index knuckle to pinky knuckle), so it works near or far from the camera.
 * A pinch counts when two fingertips close within CLOSE and then spread past OPEN (the gap between the two stops
 * jitter from counting). If two pairs both reach three pinches, the pair that got closest wins: real fingertips touch.
 */
public final class PinchDetector {

    public static final double CLOSE = 0.35;
    public static final double OPEN = 0.6;
    public static final int PINCHES = 3;
    public static final long WINDOW_MS = 5000;

    /** The pair with the most pinches so far, for the page to show "Pinch 2 of 3". */
    public record Progress(int a, int b, int count) {
    }

    private record Pinch(long t, double closest) {
    }

    private static final int[][] PAIRS = pairs();

    private final double aspect;
    private final boolean[] closed = new boolean[PAIRS.length];
    private final double[] closestNow = new double[PAIRS.length];
    private final List<Deque<Pinch>> done = new ArrayList<>();

    /** @param aspect frame height divided by width, to measure y in the same units as x */
    public PinchDetector(double aspect) {
        this.aspect = aspect;
        for (int i = 0; i < PAIRS.length; i++) {
            done.add(new ArrayDeque<>());
        }
    }

    private static int[][] pairs() {
        List<int[]> out = new ArrayList<>();
        for (int a = 0; a < 5; a++) {
            for (int b = a + 1; b < 5; b++) {
                out.add(new int[]{a, b});
            }
        }
        return out.toArray(new int[0][]);
    }

    /** Feeds one frame; returns the locked pair (finger numbers, lower first) once a pair has pinched three times. */
    public Optional<int[]> onFrame(long tMs, double[] tips, double palm) {
        if (tips == null || tips.length < 10 || palm <= 0) {
            return Optional.empty();
        }
        int best = -1;
        double bestScore = Double.MAX_VALUE;
        for (int i = 0; i < PAIRS.length; i++) {
            int a = PAIRS[i][0];
            int b = PAIRS[i][1];
            double d = Math.hypot(tips[2 * a] - tips[2 * b], (tips[2 * a + 1] - tips[2 * b + 1]) * aspect) / palm;
            if (!closed[i] && d < CLOSE) {
                closed[i] = true;
                closestNow[i] = d;
            } else if (closed[i]) {
                closestNow[i] = Math.min(closestNow[i], d);
                if (d > OPEN) {
                    closed[i] = false;
                    done.get(i).addLast(new Pinch(tMs, closestNow[i]));
                }
            }
            Deque<Pinch> q = done.get(i);
            while (!q.isEmpty() && tMs - q.peekFirst().t() > WINDOW_MS) {
                q.removeFirst();
            }
            if (q.size() >= PINCHES) {
                double score = q.stream().mapToDouble(Pinch::closest).average().orElse(Double.MAX_VALUE);
                if (score < bestScore) {
                    bestScore = score;
                    best = i;
                }
            }
        }
        if (best < 0) {
            return Optional.empty();
        }
        int[] pair = PAIRS[best].clone();
        reset();
        return Optional.of(pair);
    }

    /** The pair furthest along (most pinches in the window), or null if none has pinched yet. */
    public Progress progress() {
        int best = -1;
        for (int i = 0; i < PAIRS.length; i++) {
            int n = done.get(i).size();
            if (n > 0 && (best < 0 || n > done.get(best).size()
                    || (n == done.get(best).size() && done.get(i).peekLast().closest() < done.get(best).peekLast().closest()))) {
                best = i;
            }
        }
        return best < 0 ? null : new Progress(PAIRS[best][0], PAIRS[best][1], done.get(best).size());
    }

    public void reset() {
        for (int i = 0; i < PAIRS.length; i++) {
            closed[i] = false;
            done.get(i).clear();
        }
    }
}
