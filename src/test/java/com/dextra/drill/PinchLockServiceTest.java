package com.dextra.drill;

import com.dextra.live.LiveSocket;
import com.dextra.live.TrackPoint;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The glue: pinches only count while the picker listens, then the pair is locked and announced to the page. */
class PinchLockServiceTest {

    static final class CapturingSocket extends LiveSocket {
        final List<String> sent = new ArrayList<>();

        @Override
        public void broadcast(String json) {
            sent.add(json);
        }
    }

    /** Thumb at (0.40, 0.50), palm 0.1 wide; `partner` opens to 1.0 palm widths and closes to 0.15. */
    private static long pinches(PinchLockService s, long t, int partner, int count) {
        for (int p = 0; p <= count; p++) {
            for (int f = 0; f < 12; f++) {
                boolean closed = p < count && f >= 6;
                double[] tips = {0.40, 0.50, 0.70, 0.30, 0.60, 0.20, 0.70, 0.24, 0.80, 0.30};
                tips[2 * partner] = 0.40 + (closed ? 0.15 : 1.0) * 0.10;
                tips[2 * partner + 1] = 0.50;
                s.onPoint(new TrackPoint(t += 33, true, tips[2], tips[3], 0.95, 30, tips, 0.10));
            }
        }
        return t;
    }

    @Test
    void pinchesOnlyLockWhileListeningThenTheLockIsAnnounced() {
        FingerLock lock = new FingerLock();
        CapturingSocket socket = new CapturingSocket();
        PinchLockService service = new PinchLockService(lock, socket);

        long t = pinches(service, 0, 2, 3);                  // picker closed: nothing happens
        assertArrayEquals(new int[]{0, 1}, lock.pair());
        assertTrue(socket.sent.isEmpty());

        service.listen();
        pinches(service, t, 2, 3);                           // thumb + middle, as for someone without an index finger
        assertArrayEquals(new int[]{0, 2}, lock.pair());
        assertTrue(socket.sent.contains("{\"type\":\"pinch\",\"fingers\":[\"thumb\",\"middle\"],\"count\":2,\"of\":3}"), socket.sent.toString());
        assertEquals("{\"type\":\"fingersLocked\",\"fingers\":[\"thumb\",\"middle\"],\"lockedBy\":\"pinch\"}",
                socket.sent.get(socket.sent.size() - 1));
        assertFalse(service.listening());
    }
}
