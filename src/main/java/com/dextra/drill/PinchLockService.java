package com.dextra.drill;

import com.dextra.live.LiveSocket;
import com.dextra.live.TrackPoint;
import com.dextra.live.TrackPointListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

import java.util.Objects;
import java.util.Optional;

/**
 * Watches for the three-pinch gesture while the page's finger picker is open, then locks that pair.
 * Stops on its own after LISTEN_MS, so it never locks fingers by surprise later.
 */
@Service
public class PinchLockService implements TrackPointListener {

    public static final long LISTEN_MS = 30_000;
    private static final JsonMapper JSON = JsonMapper.builder().build();

    private final FingerLock lock;
    private final LiveSocket socket;
    private PinchDetector detector;
    private long listenUntil;
    private PinchDetector.Progress lastProgress;

    public PinchLockService(FingerLock lock, LiveSocket socket) {
        this.lock = lock;
        this.socket = socket;
    }

    public synchronized void listen() {
        detector = new PinchDetector(Scorer.ASPECT);
        listenUntil = System.currentTimeMillis() + LISTEN_MS;
        lastProgress = null;
    }

    public synchronized void stop() {
        detector = null;
    }

    public synchronized boolean listening() {
        return detector != null;
    }

    @Override
    public synchronized void onPoint(TrackPoint p) {
        if (detector == null) {
            return;
        }
        if (System.currentTimeMillis() > listenUntil) {
            detector = null;
            socket.broadcast("{\"type\":\"pinchStopped\"}");
            return;
        }
        if (!p.found()) {
            return;
        }
        Optional<int[]> locked = detector.onFrame(p.t(), p.tips(), p.palm());
        if (locked.isPresent()) {
            lock.set(locked.get()[0], locked.get()[1], "pinch");
            detector = null;
            socket.broadcast(lock.json("fingersLocked"));
            return;
        }
        PinchDetector.Progress now = detector.progress();
        if (now != null && !Objects.equals(now, lastProgress)) {
            lastProgress = now;
            socket.broadcast(progressJson(now));
        }
    }

    static String progressJson(PinchDetector.Progress pr) {
        ObjectNode n = JSON.createObjectNode().put("type", "pinch");
        n.putArray("fingers").add(FingerLock.NAMES.get(pr.a())).add(FingerLock.NAMES.get(pr.b()));
        return n.put("count", pr.count()).put("of", PinchDetector.PINCHES).toString();
    }
}
