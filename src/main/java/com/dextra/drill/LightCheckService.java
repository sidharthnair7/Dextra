package com.dextra.drill;

import com.dextra.live.TrackPoint;
import com.dextra.live.TrackPointListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/** Collects frames for a few seconds and runs the LightCheck on them. One check at a time. */
@Service
public class LightCheckService implements TrackPointListener {

    private final List<TrackPoint> collected = new ArrayList<>();
    private volatile boolean collecting;

    @Override
    public void onPoint(TrackPoint point) {
        if (collecting) {
            synchronized (collected) {
                collected.add(point);
            }
        }
    }

    public synchronized LightCheck.Result run(double seconds) throws InterruptedException {
        synchronized (collected) {
            collected.clear();
        }
        collecting = true;
        try {
            Thread.sleep((long) (seconds * 1000));
        } finally {
            collecting = false;
        }
        List<TrackPoint> copy;
        synchronized (collected) {
            copy = List.copyOf(collected);
        }
        return LightCheck.evaluate(copy, seconds);
    }
}
