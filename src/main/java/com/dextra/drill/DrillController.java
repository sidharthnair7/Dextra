package com.dextra.drill;

import com.dextra.drill.DrillTypes.AttemptSummary;
import com.dextra.drill.DrillTypes.AttemptView;
import com.dextra.drill.DrillTypes.StartResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class DrillController {

    /** Who is practising; optional, blank means "Guest". In the body, not the URL, since it's a person's name. */
    public record PersonRequest(String person) {
    }

    private final AttemptService attempts;
    private final SetupCheckService setupCheck;
    private final AttemptLog history;

    public DrillController(AttemptService attempts, SetupCheckService setupCheck, AttemptLog history) {
        this.attempts = attempts;
        this.setupCheck = setupCheck;
        this.history = history;
    }

    /** The course every drill uses, so a page can draw the targets before the first attempt. */
    @GetMapping("/api/course")
    public List<DrillTypes.Target> course() {
        return AttemptService.course();
    }

    @PostMapping("/api/attempts/start")
    public StartResponse start(@RequestBody(required = false) PersonRequest req) {
        return attempts.start(req == null ? null : req.person());
    }

    @GetMapping("/api/attempts/{id}")
    public ResponseEntity<AttemptView> get(@PathVariable String id) {
        return attempts.get(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/attempts")
    public List<AttemptSummary> list() {
        return attempts.list();
    }

    /** Clinic mode: one person's saved attempts over time, the trend in each number, and the refusals. */
    @PostMapping("/api/progress")
    public Progress.View progress(@RequestBody(required = false) PersonRequest req) {
        String person = AttemptService.cleanName(req == null ? null : req.person());
        return Progress.of(person, history.forPerson(person));
    }

    /** Everyone with saved attempts, most recent first. */
    @GetMapping("/api/people")
    public List<Map<String, Object>> people() {
        return history.people();
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
