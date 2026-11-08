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
    @Test
    void tigerConnectionStringBecomesJdbc() {
        String[] j = AttemptLog.toJdbc("postgres://tsdbadmin:p%40ss@host.example.com:33597/tsdb?sslmode=require");
        assertEquals("jdbc:postgresql://host.example.com:33597/tsdb?sslmode=require&connectTimeout=10&socketTimeout=20", j[0]);
        assertEquals("tsdbadmin", j[1]);
        assertEquals("p@ss", j[2]);
    }

    @Test
    void dailyGroupsAttemptsByDayAndAveragesOnlyScoredOnes() {
        AttemptLog log = AttemptLog.memoryOnly();
        log.append(new AttemptRecord("a", "Sam", "2026-11-07T10:00:00Z", "done", true, null, 60.0, 40.0, 2.0, 90.0, 5, 0, List.of("thumb", "index")));
        log.append(new AttemptRecord("b", "Sam", "2026-11-07T12:00:00Z", "done", true, null, 80.0, 60.0, 1.0, 100.0, 5, 0, List.of("thumb", "index")));
        log.append(new AttemptRecord("c", "Sam", "2026-11-08T09:00:00Z", "timeout", false, "course not finished (1 of 5 targets)", null, null, null, 70.0, 1, 0, List.of("thumb", "index")));
        List<AttemptLog.Day> days = log.daily("sam");
        assertEquals(2, days.size());
        assertEquals("2026-11-07", days.get(0).day());
        assertEquals(2, days.get(0).attempts());
        assertEquals(70.0, days.get(0).accuracy());
        assertEquals(1.5, days.get(0).speed());
        assertEquals(1, days.get(1).attempts());
        assertEquals(0, days.get(1).scored());
        assertNull(days.get(1).accuracy());
        assertEquals("memory", log.storage());
    }
}
