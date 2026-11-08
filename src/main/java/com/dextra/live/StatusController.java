package com.dextra.live;

import com.dextra.drill.GeminiCoach;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class StatusController {

    private final FingertipTracker tracker;
    private final GeminiCoach coach;

    public StatusController(FingertipTracker tracker, GeminiCoach coach) {
        this.tracker = tracker;
        this.coach = coach;
    }

    @GetMapping("/api/status")
    public Map<String, Object> status() {
        Map<String, Object> out = new LinkedHashMap<>();
        TrackPoint p = tracker.last();
        out.put("status", tracker.status());
        out.put("fps", p == null ? 0 : Math.round(p.fps() * 10) / 10.0);
        out.put("handFound", p != null && p.found());
        out.put("gemini", coach.online() ? "online" : "offline");
        return out;
    }
}
