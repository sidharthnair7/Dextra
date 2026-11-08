package com.dextra.drill;

import com.dextra.drill.DrillTypes.Target;
import com.dextra.live.TrackPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Builds fake fingertip paths through the course at 30 frames a second, so the scoring can be tested
 * without a camera. Each move follows a minimum-jerk curve, the textbook shape of a smooth human reach.
 */
final class SyntheticPaths {

    static final long FRAME_MS = 33;

    double wiggle = 0;        // sideways wobble amplitude, in screen widths
    int wiggleCycles = 3;
    double noise = 0;         // tracker noise (standard deviation), in normalized units
    boolean stopAndGo = false;
    int submoves = 1;         // a hesitant hand reaches in several start-stop pieces
    int dropoutMove = -1;     // move number (0-3) that loses the hand for dropoutFrames
    int dropoutFrames = 0;
    double randomDropRate = 0;
    int glitchMove = -1;      // move number that gets one impossible jump
    long seed = 42;

    List<TrackPoint> build(List<Target> course) {
        Random rnd = new Random(seed);
        List<TrackPoint> out = new ArrayList<>();
        long[] t = {0};
        Target first = course.get(0);
        for (int i = 0; i < 10; i++) {
            add(out, t, first.x(), first.y(), rnd, true);
        }
        for (int m = 0; m + 1 < course.size(); m++) {
            Target a = course.get(m);
            Target b = course.get(m + 1);
            int frames = 27; // about 0.9 s per move
            List<double[]> path = new ArrayList<>();
            if (stopAndGo) {
                double mx = (a.x() + b.x()) / 2;
                double my = (a.y() + b.y()) / 2;
                path.addAll(move(a.x(), a.y(), mx, my, frames / 2));
                for (int i = 0; i < 8; i++) {
                    path.add(new double[]{mx, my});
                }
                path.addAll(move(mx, my, b.x(), b.y(), frames / 2));
            } else if (submoves > 1) {
                double px = a.x();
                double py = a.y();
                for (int s = 1; s <= submoves; s++) {
                    double f = (double) s / submoves;
                    double nx = a.x() + (b.x() - a.x()) * f;
                    double ny = a.y() + (b.y() - a.y()) * f;
                    path.addAll(move(px, py, nx, ny, frames / submoves + 2));
                    px = nx;
                    py = ny;
                }
            } else {
                path.addAll(move(a.x(), a.y(), b.x(), b.y(), frames));
            }
            for (int i = 0; i < path.size(); i++) {
                double[] p = path.get(i);
                double x = p[0];
                double y = p[1];
                if (wiggle > 0) {
                    // offset perpendicular to the move
                    double dx = b.x() - a.x();
                    double dy = (b.y() - a.y()) * Scorer.ASPECT;
                    double len = Math.hypot(dx, dy);
                    double off = wiggle * Math.sin(2 * Math.PI * wiggleCycles * i / path.size());
                    x += -dy / len * off;
                    y += dx / len * off / Scorer.ASPECT;
                }
                boolean found = !(m == dropoutMove && i >= 5 && i < 5 + dropoutFrames) && rnd.nextDouble() >= randomDropRate;
                if (m == glitchMove && i == path.size() / 2) {
                    add(out, t, 0.97, 0.03, rnd, true);
                    continue;
                }
                add(out, t, x, y, rnd, found);
            }
        }
        Target last = course.get(course.size() - 1);
        for (int i = 0; i < 5; i++) {
            add(out, t, last.x(), last.y(), rnd, true);
        }
        return out;
    }

    private void add(List<TrackPoint> out, long[] t, double x, double y, Random rnd, boolean found) {
        double nx = x + (noise > 0 ? rnd.nextGaussian() * noise : 0);
        double ny = y + (noise > 0 ? rnd.nextGaussian() * noise : 0);
        out.add(found ? new TrackPoint(t[0], true, nx, ny, 0.97, 30) : new TrackPoint(t[0], false, 0, 0, 0, 30));
        t[0] += FRAME_MS;
    }

    /** Minimum-jerk interpolation: s(u) = 10u^3 - 15u^4 + 6u^5. */
    static List<double[]> move(double x0, double y0, double x1, double y1, int frames) {
        List<double[]> pts = new ArrayList<>();
        for (int i = 1; i <= frames; i++) {
            double u = (double) i / frames;
            double s = 10 * Math.pow(u, 3) - 15 * Math.pow(u, 4) + 6 * Math.pow(u, 5);
            pts.add(new double[]{x0 + (x1 - x0) * s, y0 + (y1 - y0) * s});
        }
        return pts;
    }
}
