package com.dextra.drill;

import com.dextra.drill.DrillTypes.Metric;
import com.dextra.drill.DrillTypes.ScoreReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Turns a finished attempt's numbers into two or three sentences of coaching.
 * Gemini only ever sees the scored numbers, never the camera. With no API key, or if the call fails,
 * the offline coach writes the text from the same numbers, so the demo never depends on the network.
 */
@Service
public class GeminiCoach {

    private static final Logger log = LoggerFactory.getLogger(GeminiCoach.class);
    private static final JsonMapper JSON = JsonMapper.builder().build();

    /** The coach runs in the background, so it can wait longer than the setup check, where someone is waiting. */
    static final Duration COACH_TIMEOUT = Duration.ofSeconds(25);
    static final Duration SETUP_TIMEOUT = Duration.ofSeconds(10);

    private final String apiKey;
    private final List<String> models;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

    /**
     * Default gemini-2.5-flash: on Nov 7 it answered in about 1 s while gemini-flash-latest (3.8 Flash)
     * was overloaded (503 "high demand", 35 to 60 s, timeouts). The fallback is tried when the main model fails.
     */
    public GeminiCoach(@Value("${dextra.gemini.api-key:${GEMINI_API_KEY:}}") String apiKey,
                       @Value("${dextra.gemini.model:gemini-2.5-flash}") String model,
                       @Value("${dextra.gemini.fallback-model:gemini-flash-latest}") String fallbackModel) {
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.models = fallbackModel == null || fallbackModel.isBlank() || fallbackModel.equals(model)
                ? List.of(model) : List.of(model, fallbackModel);
    }

    public boolean online() {
        return !apiKey.isEmpty();
    }

    /** Coaching text for one attempt. Never throws; an unscored attempt gets the refusal explained, not coached. */
    public String coach(ScoreReport report, String state) {
        if (!report.scored()) {
            return "Not scored: " + report.refusedReason() + ". Check the lighting and keep your hand in view, then try again.";
        }
        if (online()) {
            try {
                return "Gemini: " + askGemini(prompt(report, state), null, COACH_TIMEOUT);
            } catch (Exception e) {
                log.warn("Gemini call failed, using the offline coach: {}", e.toString());
            }
        }
        return "Offline coach: " + offline(report);
    }

    static String prompt(ScoreReport report, String state) {
        StringBuilder sb = new StringBuilder();
        sb.append("You coach a fingertip precision drill: the user moves an index fingertip through 5 on-screen targets in order, ")
          .append("tracked by a webcam. You only see the measured numbers below. Write 2 or 3 short plain sentences: ")
          .append("one thing that went well, the one number to work on, and one concrete thing to try next attempt. ")
          .append("Quote the numbers you use. Do not claim anything about surgery, medical skill or grading, ")
          .append("and do not guess at anything the numbers don't show.\n\n");
        sb.append("Attempt ended: ").append(state).append('\n');
        for (Map.Entry<String, Metric> e : report.metrics().entrySet()) {
            Metric m = e.getValue();
            sb.append("- ").append(e.getKey()).append(": ").append(m.value()).append(' ').append(m.unit())
              .append(" (").append(m.formula()).append(')');
            if (m.detail() != null) {
                sb.append("; ").append(m.detail());
            }
            sb.append('\n');
        }
        sb.append("Unscored stretches: ").append(report.refusals().size()).append('\n');
        return sb.toString();
    }

    /**
     * Explains a setup check. The pass or fail was already decided by LightCheck's measurements;
     * Gemini only looks at one camera frame to say what is likely causing weak tracking and how to fix it.
     */
    public String explainSetup(LightCheck.Result result, byte[] jpeg) {
        if (online() && jpeg != null && jpeg.length > 0) {
            try {
                return "Gemini: " + askGemini(setupPrompt(result), jpeg, SETUP_TIMEOUT);
            } catch (Exception e) {
                log.warn("Gemini setup check failed, using the offline advice: {}", e.toString());
            }
        }
        return "Offline check: " + offlineSetup(result);
    }

    static String setupPrompt(LightCheck.Result r) {
        return "This is one webcam frame from a fingertip-tracking drill, taken while the user held their hand still. "
                + "The setup check was already decided by measurement and the result is " + (r.pass() ? "PASS" : "FAIL") + ". "
                + "Do not change that decision. Measured: hand found in " + r.coveragePercent() + "% of frames (needs "
                + Math.round(LightCheck.MIN_COVERAGE * 100) + "%), " + r.fps() + " frames per second (needs "
                + Math.round(LightCheck.MIN_FPS) + "), fingertip jitter " + (r.jitterPx() == null ? "not measured" : r.jitterPx() + " px")
                + " (limit " + LightCheck.MAX_JITTER_PX + " px). Failed checks: "
                + (r.reasons().isEmpty() ? "none" : String.join("; ", r.reasons())) + ". "
                + "In 2 short plain sentences: name what in the image most likely explains these numbers "
                + "(lighting, backlight, a busy background, the hand too close or too far, or the hand partly out of frame), "
                + "then give one concrete fix. If it passed, confirm the setup in one sentence and give one tip to keep it that way. "
                + "Only describe what is visible; never comment on the person.";
    }

    /** Plain fixes for each measured failure, for when Gemini isn't available. */
    static String offlineSetup(LightCheck.Result r) {
        if (r.pass()) {
            return String.format(Locale.ROOT,
                    "Setup looks good: hand found in %.0f%% of frames at %.0f fps. Keep the same light and distance for the drill.",
                    r.coveragePercent(), r.fps());
        }
        if (r.frames() == 0) {
            return "No frames from the camera. Check that no other app is using the webcam, then restart DEXTRA.";
        }
        List<String> fixes = new ArrayList<>();
        if (r.coveragePercent() < LightCheck.MIN_COVERAGE * 100) {
            fixes.add("keep your whole hand in view with your index finger pointing up, about an arm's length from the camera");
        }
        if (r.fps() < LightCheck.MIN_FPS) {
            fixes.add("close other apps that use the camera or CPU, and plug the laptop in");
        }
        if (r.jitterPx() != null && r.jitterPx() > LightCheck.MAX_JITTER_PX) {
            fixes.add("add light in front of you (not behind you) and rest your elbow on the desk");
        }
        return "Not ready: " + String.join("; ", r.reasons()) + ". Try this: " + String.join("; ", fixes) + ".";
    }

    /** Tries each model in turn (main, then fallback), each with its own timeout. */
    private String askGemini(String prompt, byte[] jpeg, Duration timeout) throws Exception {
        String body = JSON.writeValueAsString(requestBody(prompt, jpeg));
        Exception last = null;
        for (String m : models) {
            try {
                return callModel(m, body, timeout);
            } catch (Exception e) {
                log.warn("Gemini {} failed: {}", m, e.toString());
                last = e;
            }
        }
        throw last;
    }

    /** Thinking is off (budget 0): these are short answers, and thinking made replies slow. */
    static ObjectNode requestBody(String prompt, byte[] jpeg) {
        ObjectNode body = JSON.createObjectNode();
        var parts = body.putArray("contents").addObject().putArray("parts");
        if (jpeg != null) {
            parts.addObject().putObject("inline_data")
                    .put("mime_type", "image/jpeg")
                    .put("data", Base64.getEncoder().encodeToString(jpeg));
        }
        parts.addObject().put("text", prompt);
        ObjectNode gen = body.putObject("generationConfig").put("maxOutputTokens", 1024).put("temperature", 0.4);
        gen.putObject("thinkingConfig").put("thinkingBudget", 0);
        return body;
    }

    private String callModel(String m, String body, Duration timeout) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(
                        "https://generativelanguage.googleapis.com/v1beta/models/" + m + ":generateContent"))
                .timeout(timeout)
                .header("Content-Type", "application/json")
                .header("x-goog-api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200) {
            throw new IllegalStateException("HTTP " + res.statusCode() + ": " + res.body());
        }
        JsonNode replyParts = JSON.readTree(res.body()).path("candidates").path(0).path("content").path("parts");
        StringBuilder text = new StringBuilder();
        for (JsonNode p : replyParts) {
            text.append(p.path("text").asString(""));
        }
        String out = text.toString().trim();
        if (out.isEmpty()) {
            throw new IllegalStateException("empty reply: " + res.body());
        }
        return out;
    }

    /** Rule-based coaching from the same numbers: the weakest of accuracy and smoothness, plus speed. */
    static String offline(ScoreReport report) {
        double acc = value(report, "accuracy");
        double smooth = value(report, "smoothness");
        double speed = value(report, "speed");
        String good = acc >= smooth
                ? String.format(Locale.ROOT, "Accuracy was your strong point at %.0f.", acc)
                : String.format(Locale.ROOT, "Smoothness was your strong point at %.0f.", smooth);
        String work;
        if (Math.min(acc, smooth) >= 90) {
            work = String.format(Locale.ROOT,
                    "Both scores are high, so work on speed (%.1f s per target): try the next run a little faster without dropping below 90.",
                    speed);
        } else if (smooth < acc) {
            work = String.format(Locale.ROOT,
                    "Work on smoothness (%.0f): move in one steady glide between targets instead of stopping and correcting.", smooth);
        } else {
            work = String.format(Locale.ROOT,
                    "Work on accuracy (%.0f): slow down for the last few centimetres and settle on the centre of each target.", acc);
        }
        return good + " " + work;
    }

    private static double value(ScoreReport r, String key) {
        Metric m = r.metrics().get(key);
        return m == null || m.value() == null ? 0 : m.value();
    }
}
