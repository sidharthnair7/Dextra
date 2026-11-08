package com.dextra.drill;

import java.util.List;

/**
 * One finished attempt, as saved for the progress review. The numbers are null when the attempt wasn't scored;
 * refusedReason then says why, so a refusal stays visible in the history instead of disappearing.
 */
public record AttemptRecord(String id, String person, String startedAt, String state, boolean scored, String refusedReason,
                            Double accuracy, Double smoothness, Double speed, Double coverage,
                            int hits, int refusals, List<String> fingers) {
}
