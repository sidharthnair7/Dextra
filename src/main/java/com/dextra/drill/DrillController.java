package com.dextra.drill;

import com.dextra.drill.DrillTypes.AttemptSummary;
import com.dextra.drill.DrillTypes.AttemptView;
import com.dextra.drill.DrillTypes.StartResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DrillController {

    private final AttemptService attempts;
    private final SetupCheckService setupCheck;

    public DrillController(AttemptService attempts, SetupCheckService setupCheck) {
        this.attempts = attempts;
        this.setupCheck = setupCheck;
    }

    /** The course every drill uses, so a page can draw the targets before the first attempt. */
    @GetMapping("/api/course")
    public List<DrillTypes.Target> course() {
        return AttemptService.course();
    }

    @PostMapping("/api/attempts/start")
    public StartResponse start() {
        return attempts.start();
    }

    @GetMapping("/api/attempts/{id}")
    public ResponseEntity<AttemptView> get(@PathVariable String id) {
        return attempts.get(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/attempts")
    public List<AttemptSummary> list() {
        return attempts.list();
    }

    /**
     * Hold the hand still for `seconds` (default 3, max 10). Returns pass or fail with the measured reasons,
     * plus advice: Gemini reads one frame when a key is set, otherwise the offline check.
     */
    @PostMapping("/api/check")
    public SetupCheckService.Response check(@RequestParam(defaultValue = "3") double seconds) throws InterruptedException {
        return setupCheck.run(Math.max(0.1, Math.min(10, seconds)));
    }
}
