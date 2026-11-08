package com.dextra.drill;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProgressTest {

    private static AttemptRecord scored(String id, double acc, double smooth, double speed, double cov) {
        return new AttemptRecord(id, "Sam", "t" + id, "done", true, null, acc, smooth, speed, cov, 5, 0,
                List.of("thumb", "index"));
    }

    @Test
    void trendsUseScoredAttemptsAndRefusalsAreCounted() {
        AttemptRecord refused = new AttemptRecord("0", "Sam", "t0", "timeout", false,
                "course not finished (2 of 5 targets)", null, null, null, 70.0, 2, 1, List.of("thumb", "index"));
        Progress.View v = Progress.of("Sam", List.of(refused,
                scored("1", 60, 40, 2.0, 90), scored("2", 75, 55, 1.6, 95), scored("3", 81, 50, 1.4, 100)));

        assertEquals(4, v.attempts());
        assertEquals(3, v.scored());
        assertEquals(1, v.refused());

        Progress.MetricTrend acc = v.metrics().get(0);
        assertEquals(List.of(60.0, 75.0, 81.0), acc.values());
        assertEquals(60.0, acc.first());
        assertEquals(81.0, acc.latest());
        assertEquals(81.0, acc.best());
        assertEquals(21.0, acc.change());
        assertEquals(Boolean.TRUE, acc.improved());

        Progress.MetricTrend smooth = v.metrics().get(1);
        assertEquals(55.0, smooth.best());
        assertEquals(10.0, smooth.change());

        Progress.MetricTrend speed = v.metrics().get(2);                     // lower is better
        assertFalse(speed.higherIsBetter());
        assertEquals(1.4, speed.best());
        assertEquals(-0.6, speed.change());
        assertEquals(Boolean.TRUE, speed.improved());

        assertEquals(List.of(90.0, 95.0, 100.0), v.metrics().get(3).values()); // the refused attempt's 70% isn't a point
    }

    @Test
    void gettingWorseIsSaidPlainly() {
        Progress.View v = Progress.of("Sam", List.of(scored("1", 80, 60, 1.2, 100), scored("2", 70, 50, 1.5, 100)));
        assertEquals(Boolean.FALSE, v.metrics().get(0).improved());          // accuracy went down
        assertEquals(Boolean.FALSE, v.metrics().get(2).improved());          // slower
        assertNull(v.metrics().get(3).improved());                           // no change
    }

    @Test
    void oneAttemptHasNoChangeYetAndNoAttemptsHaveNoNumbers() {
        Progress.MetricTrend one = Progress.of("Sam", List.of(scored("1", 60, 40, 2.0, 90))).metrics().get(0);
        assertEquals(60.0, one.latest());
        assertNull(one.change());
        Progress.View none = Progress.of("Nobody", List.of());
        assertEquals(0, none.attempts());
        assertNull(none.metrics().get(0).latest());
    }
}
