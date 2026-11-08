package com.dextra.drill;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * The progress review: one person's attempts over time, the trend in each number, and how many were refused.
 * Trends only use scored attempts; a refused attempt is counted and listed, never turned into a number.
 */
public final class Progress {

    private Progress() {
    }

    /** One number over time. change is latest minus first; improved says whether that's better (speed: lower is better). */
    public record MetricTrend(String key, String label, String unit, boolean higherIsBetter, List<Double> values,
                              Double first, Double latest, Double best, Double change, Boolean improved) {
    }

    public record View(String person, int attempts, int scored, int refused, List<MetricTrend> metrics,
                       List<AttemptRecord> history, List<AttemptLog.Day> daily, String storage) {
    }

    public static View of(String person, List<AttemptRecord> history) {
        return of(person, history, List.of(), null);
    }

    public static View of(String person, List<AttemptRecord> history, List<AttemptLog.Day> daily, String storage) {
        int scored = (int) history.stream().filter(AttemptRecord::scored).count();
        List<MetricTrend> metrics = List.of(
                trend("accuracy", "Accuracy", "score", true, history, AttemptRecord::accuracy),
                trend("smoothness", "Smoothness", "score", true, history, AttemptRecord::smoothness),
                trend("speed", "Speed", "s per target", false, history, AttemptRecord::speed),
                trend("coverage", "Tracked", "% of frames", true, history, AttemptRecord::coverage));
        return new View(person, history.size(), scored, history.size() - scored, metrics, history, daily, storage);
    }

    private static MetricTrend trend(String key, String label, String unit, boolean higherIsBetter,
                                     List<AttemptRecord> history, Function<AttemptRecord, Double> get) {
        List<Double> values = new ArrayList<>();
        for (AttemptRecord r : history) {
            Double v = get.apply(r);
            if (r.scored() && v != null) {
                values.add(v);
            }
        }
        if (values.isEmpty()) {
            return new MetricTrend(key, label, unit, higherIsBetter, values, null, null, null, null, null);
        }
        double first = values.get(0);
        double latest = values.get(values.size() - 1);
        double best = higherIsBetter
                ? values.stream().mapToDouble(Double::doubleValue).max().orElse(latest)
                : values.stream().mapToDouble(Double::doubleValue).min().orElse(latest);
        Double change = values.size() < 2 ? null : Math.round((latest - first) * 10) / 10.0;
        Boolean improved = change == null || change == 0 ? null : (higherIsBetter ? change > 0 : change < 0);
        return new MetricTrend(key, label, unit, higherIsBetter, values, first, latest, best, change, improved);
    }
}
