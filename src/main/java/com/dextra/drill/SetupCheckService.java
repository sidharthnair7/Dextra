package com.dextra.drill;

import com.dextra.live.FingertipTracker;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * The setup check before a drill: LightCheck measures and decides pass or fail, then one camera frame
 * and those numbers go to the coach, which explains the likely cause and a fix.
 */
@Service
public class SetupCheckService {

    /** What /api/check returns: LightCheck's fields unchanged, plus the advice. */
    public record Response(boolean pass, Double jitterPx, double coveragePercent, double fps, int frames,
                           List<String> reasons, String advice, boolean geminiSawFrame) {
    }

    private final LightCheckService lightCheck;
    private final FingertipTracker tracker;
    private final GeminiCoach coach;

    public SetupCheckService(LightCheckService lightCheck, FingertipTracker tracker, GeminiCoach coach) {
        this.lightCheck = lightCheck;
        this.tracker = tracker;
        this.coach = coach;
    }

    public Response run(double seconds) throws InterruptedException {
        LightCheck.Result r = lightCheck.run(seconds);
        byte[] jpeg = null;
        if (r.frames() > 0) {
            try {
                jpeg = tracker.snapshot().get(1, TimeUnit.SECONDS);
            } catch (Exception e) {
                jpeg = null;
            }
        }
        String advice = coach.explainSetup(r, jpeg);
        return new Response(r.pass(), r.jitterPx(), r.coveragePercent(), r.fps(), r.frames(), r.reasons(), advice,
                advice.startsWith("Gemini:"));
    }
}
