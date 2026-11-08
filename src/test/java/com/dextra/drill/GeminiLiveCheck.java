package com.dextra.drill;

import com.dextra.drill.DrillTypes.Metric;
import com.dextra.drill.DrillTypes.ScoreReport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * A real call to Gemini with the key in .env, numbers only (no camera frame). Not part of the normal build;
 * run it before a demo to check the key and the model answer quickly:
 *   DEXTRA_LIVE_GEMINI=1 mvn test -Dtest=GeminiLiveCheck
 */
@EnabledIfEnvironmentVariable(named = "DEXTRA_LIVE_GEMINI", matches = "1")
class GeminiLiveCheck {

    @Test
    void realGeminiCoachesFromTheNumbers() throws Exception {
        String key = Files.readAllLines(Path.of(".env")).stream()
                .filter(l -> l.startsWith("GEMINI_API_KEY="))
                .map(l -> l.substring("GEMINI_API_KEY=".length()).trim())
                .findFirst().orElse("");
        assumeTrue(!key.isEmpty(), "no GEMINI_API_KEY in .env");

        Map<String, Metric> m = new LinkedHashMap<>();
        m.put("accuracy", new Metric(81.0, "score", "100 minus mean distance off the ideal path", null));
        m.put("smoothness", new Metric(33.0, "score", "share of moves without a stop and restart", null));
        m.put("speed", new Metric(1.1, "s per target", "moving time divided by targets", null));
        m.put("coverage", new Metric(100.0, "%", "frames with the hand found", null));
        ScoreReport report = new ScoreReport(true, null, m, List.of());

        GeminiCoach coach = new GeminiCoach(key, "gemini-2.5-flash", "gemini-flash-latest");
        long t0 = System.nanoTime();
        String text = coach.coach(report, "done");
        System.out.printf("live coach answered in %.1f s: %s%n", (System.nanoTime() - t0) / 1e9, text);
        assertTrue(text.startsWith("Gemini:"), text);
    }
}
