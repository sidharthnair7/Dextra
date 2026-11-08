package com.dextra.drill;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * A real round trip to Tiger Cloud with TIGER_DATABASE_URL from .env. Not part of the normal build; run it to check
 * the database before a demo: DEXTRA_LIVE_TIGER=1 mvn test -Dtest=TigerLiveCheck
 * It writes under a test person name, so it never mixes with real practice.
 */
@EnabledIfEnvironmentVariable(named = "DEXTRA_LIVE_TIGER", matches = "1")
class TigerLiveCheck {

    @Test
    void attemptGoesInAndComesBackWithItsDay() throws Exception {
        String url = Files.readAllLines(Path.of(".env")).stream().filter(l -> l.startsWith("TIGER_DATABASE_URL="))
                .map(l -> l.substring("TIGER_DATABASE_URL=".length()).trim()).findFirst().orElse("");
        assumeTrue(!url.isEmpty(), "no TIGER_DATABASE_URL in .env");
        AttemptLog log = new AttemptLog("", url);
        assertEquals("Tiger Cloud", log.storage());
        String person = "Live check " + System.nanoTime();
        log.append(new AttemptRecord("live", person, Instant.now().toString(), "done", true, null, 77.0, 55.0, 1.4, 96.0, 5, 0, List.of("thumb", "index")));
        List<AttemptRecord> back = log.forPerson(person);
        assertEquals(1, back.size());
        assertEquals(77.0, back.get(0).accuracy());
        List<AttemptLog.Day> days = log.daily(person);
        assertEquals(1, days.size());
        System.out.println("Tiger Cloud round trip ok: " + back.get(0) + " / " + days.get(0));

        // Clean up, so live-check rows never show up in clinic mode
        String[] j = AttemptLog.toJdbc(url);
        try (java.sql.Connection c = java.sql.DriverManager.getConnection(j[0], j[1], j[2]);
             java.sql.Statement s = c.createStatement()) {
            int gone = s.executeUpdate("DELETE FROM " + AttemptLog.TABLE + " WHERE person LIKE 'Live check %'");
            System.out.println("Removed " + gone + " live-check rows");
        }
        assertEquals(0, log.forPerson(person).size());
    }
}
