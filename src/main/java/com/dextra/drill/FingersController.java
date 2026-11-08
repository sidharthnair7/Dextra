package com.dextra.drill;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/** Which two fingers this person uses: read it, tap-pick it, or start and stop watching for the three-pinch gesture. */
@RestController
public class FingersController {

    public record FingersRequest(List<String> fingers) {
    }

    private final FingerLock lock;
    private final PinchLockService pinch;

    public FingersController(FingerLock lock, PinchLockService pinch) {
        this.lock = lock;
        this.pinch = pinch;
    }

    @GetMapping("/api/fingers")
    public Map<String, Object> get() {
        return lock.view();
    }

    /** The tap path: {"fingers": ["thumb", "middle"]}. Always works, including for anyone who can't pinch. */
    @PostMapping("/api/fingers")
    public ResponseEntity<Map<String, Object>> set(@RequestBody FingersRequest req) {
        if (req == null || req.fingers() == null || req.fingers().size() != 2) {
            return ResponseEntity.badRequest().body(Map.of("error", "pick exactly two fingers"));
        }
        try {
            lock.set(req.fingers().get(0), req.fingers().get(1), "tap");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
        pinch.stop();
        return ResponseEntity.ok(lock.view());
    }

    @PostMapping("/api/fingers/listen")
    public Map<String, Object> listen() {
        pinch.listen();
        return Map.of("listening", true, "seconds", PinchLockService.LISTEN_MS / 1000);
    }

    @PostMapping("/api/fingers/listen/stop")
    public Map<String, Object> stopListening() {
        pinch.stop();
        return Map.of("listening", false);
    }
}
