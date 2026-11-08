package com.dextra.drill;

import com.dextra.live.TrackPoint;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LightCheckTest {

    private static List<TrackPoint> still(double noise, double dropRate, int frames) {
        Random rnd = new Random(7);
        List<TrackPoint> pts = new ArrayList<>();
        for (int i = 0; i < frames; i++) {
            boolean found = rnd.nextDouble() >= dropRate;
            pts.add(found ? new TrackPoint(i * 33L, true, 0.5 + rnd.nextGaussian() * noise, 0.5 + rnd.nextGaussian() * noise, 0.95, 30)
                    : new TrackPoint(i * 33L, false, 0, 0, 0, 30));
        }
        return pts;
    }

    @Test
    void steadyHandPasses() {
        LightCheck.Result r = LightCheck.evaluate(still(0.002, 0, 90), 3);
        System.out.println("steady: " + r);
        assertTrue(r.pass(), r.reasons().toString());
        assertTrue(r.jitterPx() < 3);
    }

    @Test
    void shakyTrackingFailsWithItsMeasurement() {
        LightCheck.Result r = LightCheck.evaluate(still(0.01, 0, 90), 3);
        System.out.println("shaky: " + r);
        assertFalse(r.pass());
        assertTrue(r.reasons().get(0).contains("while held still"));
    }

    @Test
    void handOftenMissingFails() {
        LightCheck.Result r = LightCheck.evaluate(still(0.002, 0.5, 90), 3);
        assertFalse(r.pass());
        assertTrue(r.reasons().stream().anyMatch(s -> s.contains("hand found in only")));
    }

    @Test
    void slowCameraFails() {
        LightCheck.Result r = LightCheck.evaluate(still(0.002, 0, 30), 3);
        assertFalse(r.pass());
        assertTrue(r.reasons().stream().anyMatch(s -> s.contains("frames per second")));
    }

    @Test
    void noFramesFails() {
        LightCheck.Result r = LightCheck.evaluate(List.of(), 3);
        assertFalse(r.pass());
        assertEquals(List.of("no frames from the camera"), r.reasons());
    }
}
