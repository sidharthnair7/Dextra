package com.dextra.drill;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AttemptLogTest {

    @TempDir
    Path dir;

    private static AttemptRecord rec(String id, String person, boolean scored, Double accuracy) {
        return new AttemptRecord(id, person, "2026-11-07T21:0" + id + ":00Z", scored ? "done" : "timeout", scored,
                scored ? null : "course not finished (1 of 5 targets)", accuracy, scored ? 50.0 : null,
                scored ? 1.5 : null, 90.0, scored ? 5 : 1, 0, List.of("thumb", "index"));
    }

    @Test
    void savedAttemptsComeBackAfterARestart() {
        AttemptLog log = new AttemptLog(dir.toString());
        log.append(rec("1", "Sam", false, null));
        log.append(rec("2", "Ana", true, 70.0));
        log.append(rec("3", "sam", true, 81.0));

        AttemptLog reloaded = new AttemptLog(dir.toString());
        List<AttemptRecord> sam = reloaded.forPerson("SAM");
        assertEquals(2, sam.size());
        assertEquals("1", sam.get(0).id());                                  // oldest first
        assertNull(sam.get(0).accuracy());                                   // a refusal stays a refusal
        assertEquals("course not finished (1 of 5 targets)", sam.get(0).refusedReason());
        assertEquals(81.0, sam.get(1).accuracy());
        assertEquals(List.of("thumb", "index"), sam.get(1).fingers());

        List<Map<String, Object>> people = reloaded.people();
        assertEquals("sam", people.get(0).get("person"));                    // most recent first
        assertEquals(2, people.get(0).get("attempts"));
        assertEquals("Ana", people.get(1).get("person"));
    }

    @Test
    void anUnreadableLineIsSkippedNotFatal() throws Exception {
        Files.writeString(dir.resolve("attempts.jsonl"), "not json\n");
        new AttemptLog(dir.toString()).append(rec("1", "Sam", true, 60.0));
        assertEquals(1, new AttemptLog(dir.toString()).forPerson("Sam").size());
    }

    @Test
    void memoryOnlyKeepsAttemptsWithoutAFile() {
        AttemptLog log = AttemptLog.memoryOnly();
        log.append(rec("1", "Sam", true, 60.0));
        assertEquals(1, log.forPerson("Sam").size());
    }
}
