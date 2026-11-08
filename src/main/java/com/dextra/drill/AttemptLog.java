package com.dextra.drill;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Every finished attempt, one JSON line each, in ~/.dextra/attempts.jsonl by default: it survives restarts,
 * stays on this machine, and stays out of the repo. An empty data dir keeps the log in memory only (tests).
 */
@Component
public class AttemptLog {

    private static final Logger log = LoggerFactory.getLogger(AttemptLog.class);
    private static final JsonMapper JSON = JsonMapper.builder().build();

    private final Path file;
    private final List<AttemptRecord> records = new ArrayList<>();

    public AttemptLog(@Value("${dextra.data.dir:${user.home}/.dextra}") String dir) {
        this.file = dir == null || dir.isBlank() ? null : Path.of(dir, "attempts.jsonl");
        load();
    }

    public static AttemptLog memoryOnly() {
        return new AttemptLog("");
    }

    private void load() {
        if (file == null || !Files.exists(file)) {
            return;
        }
        try {
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                if (line.isBlank()) {
                    continue;
                }
                try {
                    records.add(JSON.readValue(line, AttemptRecord.class));
                } catch (Exception e) {
                    log.warn("Skipping an unreadable line in {}", file);
                }
            }
        } catch (Exception e) {
            log.warn("Could not read {}: {}", file, e.toString());
        }
    }

    public synchronized void append(AttemptRecord r) {
        records.add(r);
        if (file == null) {
            return;
        }
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, JSON.writeValueAsString(r) + "\n", StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception e) {
            log.warn("Could not save the attempt to {}: {}", file, e.toString());
        }
    }

    /** One person's attempts, oldest first. Names match without caring about case. */
    public synchronized List<AttemptRecord> forPerson(String person) {
        List<AttemptRecord> out = new ArrayList<>();
        for (AttemptRecord r : records) {
            if (r.person() != null && r.person().equalsIgnoreCase(person)) {
                out.add(r);
            }
        }
        return out;
    }

    /** Everyone who has practised, most recent first, with how many attempts each. */
    public synchronized List<Map<String, Object>> people() {
        Map<String, Map<String, Object>> byName = new LinkedHashMap<>();
        for (int i = records.size() - 1; i >= 0; i--) {
            AttemptRecord r = records.get(i);
            String key = r.person() == null ? "" : r.person().toLowerCase();
            Map<String, Object> p = byName.computeIfAbsent(key, k -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("person", r.person());
                m.put("attempts", 0);
                m.put("last", r.startedAt());
                return m;
            });
            p.put("attempts", (Integer) p.get("attempts") + 1);
        }
        return new ArrayList<>(byName.values());
    }
}
