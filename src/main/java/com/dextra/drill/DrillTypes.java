package com.dextra.drill;

import java.util.List;
import java.util.Map;

/** The data shapes of a drill, as the frontend receives them (see the contract in the frontend prompt). */
public final class DrillTypes {

    private DrillTypes() {
    }

    /** A target, in 0..1 screen coordinates; r is the hit radius as a share of the screen width. */
    public record Target(int id, double x, double y, double r) {
    }

    /** One recorded frame. t is milliseconds since the attempt started. */
    public record Point(long t, double x, double y, double c, boolean ok) {
    }

    public record Hit(int targetId, long t) {
    }

    /** A stretch that was not scored, and why. */
    public record Refusal(long fromT, long toT, String reason) {
    }

    /** One number, with the formula and the raw detail behind it. value is null when the attempt wasn't scored. */
    public record Metric(Double value, String unit, String formula, String detail) {
    }

    public record ScoreReport(boolean scored, String refusedReason, Map<String, Metric> metrics, List<Refusal> refusals) {
    }

    public record AttemptView(String attemptId, String startedAt, String state, List<Target> targets, List<Point> points,
                              List<Hit> hits, boolean scored, String refusedReason, Map<String, Metric> metrics,
                              List<Refusal> refusals, String feedback) {
    }

    public record AttemptSummary(String attemptId, String startedAt, String state, Double accuracy, Double smoothness) {
    }

    public record StartResponse(String attemptId, List<Target> targets) {
    }
}
