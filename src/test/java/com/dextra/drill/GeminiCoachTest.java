package com.dextra.drill;

import com.dextra.drill.DrillTypes.Metric;
import com.dextra.drill.DrillTypes.ScoreReport;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeminiCoachTest {

    private final GeminiCoach offlineCoach = new GeminiCoach("", "gemini-2.5-flash", "gemini-flash-latest");

    private static ScoreReport scored(double acc, double smooth, double speed) {
        Map<String, Metric> m = new LinkedHashMap<>();
        m.put("accuracy", new Metric(acc, "score", "f", null));
        m.put("smoothness", new Metric(smooth, "score", "f", null));
        m.put("speed", new Metric(speed, "s per target", "f", null));
        m.put("coverage", new Metric(100.0, "%", "f", null));
        return new ScoreReport(true, null, m, List.of());
    }

    @Test
    void noKeyMeansOfflineCoachNamesTheWeakNumber() {
        assertFalse(offlineCoach.online());
        String text = offlineCoach.coach(scored(81, 33, 1.1), "done");
        System.out.println(text);
        assertTrue(text.startsWith("Offline coach:"));
        assertTrue(text.contains("smoothness (33)"), text);
    }

    @Test
    void highScoresPointAtSpeed() {
        String text = offlineCoach.coach(scored(98, 95, 0.9), "done");
        assertTrue(text.contains("speed (0.9 s per target)"), text);
    }

    @Test
    void unscoredAttemptIsExplainedNotCoached() {
        ScoreReport refused = new ScoreReport(false, "not enough reliable tracking to score: 75% of frames, needs 80%",
                Map.of(), List.of());
        String text = offlineCoach.coach(refused, "done");
        assertTrue(text.startsWith("Not scored: not enough reliable tracking"), text);
    }

    @Test
    void promptCarriesOnlyNumbersAndTheGuardrail() {
        String p = GeminiCoach.prompt(scored(81, 33, 1.1), "timeout");
        assertTrue(p.contains("accuracy: 81.0 score"));
        assertTrue(p.contains("Attempt ended: timeout"));
        assertTrue(p.contains("Do not claim anything about surgery"));
    }

    @Test
    void feedbackMessageIsValidJson() throws Exception {
        String json = AttemptService.feedbackJson("ab12", "Line one \"quoted\"\nline two");
        var node = JsonMapper.builder().build().readTree(json);
        assertEquals("feedback", node.path("type").asString());
        assertEquals("Line one \"quoted\"\nline two", node.path("text").asString());
    }
    @Test
    void setupPromptKeepsJavasDecision() {
        LightCheck.Result fail = new LightCheck.Result(false, 7.8, 100.0, 30.0, 90,
                List.of("fingertip moves 7.8 px while held still (limit 3.0)"));
        String p = GeminiCoach.setupPrompt(fail);
        assertTrue(p.contains("result is FAIL"), p);
        assertTrue(p.contains("Do not change that decision"), p);
        assertTrue(p.contains("fingertip jitter 7.8 px"), p);
    }

    @Test
    void offlineSetupGivesAFixPerFailure() {
        LightCheck.Result shaky = new LightCheck.Result(false, 7.8, 60.0, 30.0, 90,
                List.of("hand found in only 60% of frames (needs 90%)", "fingertip moves 7.8 px while held still (limit 3.0)"));
        String text = offlineCoach.explainSetup(shaky, new byte[]{1, 2, 3});
        System.out.println(text);
        assertTrue(text.startsWith("Offline check: Not ready:"), text);
        assertTrue(text.contains("whole hand in view"), text);
        assertTrue(text.contains("light in front of you"), text);

        LightCheck.Result good = new LightCheck.Result(true, 1.6, 100.0, 30.0, 90, List.of());
        assertTrue(offlineCoach.explainSetup(good, null).contains("Setup looks good"));
    }
    @Test
    void requestTurnsThinkingOffAndOnlySendsAnImageWhenGiven() {
        var withImage = GeminiCoach.requestBody("explain", new byte[]{1, 2, 3});
        assertEquals(0, withImage.path("generationConfig").path("thinkingConfig").path("thinkingBudget").asInt(-1));
        assertEquals(1024, withImage.path("generationConfig").path("maxOutputTokens").asInt());
        assertEquals("image/jpeg", withImage.path("contents").path(0).path("parts").path(0).path("inline_data").path("mime_type").asString());

        var numbersOnly = GeminiCoach.requestBody("coach", null);
        assertEquals(1, numbersOnly.path("contents").path(0).path("parts").size());
        assertEquals("coach", numbersOnly.path("contents").path(0).path("parts").path(0).path("text").asString());
    }
}
