package com.dextra.drill;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Every finished attempt. With a Tiger Cloud connection string (TIGER_DATABASE_URL) attempts go into a Timescale
 * hypertable, and the daily view uses time_bucket. Without one, or if the database can't be reached at startup,
 * they go to ~/.dextra/attempts.jsonl on this machine. An empty data dir and no database keeps them in memory (tests).
 */
@Component
public class AttemptLog {

    private static final Logger log = LoggerFactory.getLogger(AttemptLog.class);
    private static final JsonMapper JSON = JsonMapper.builder().build();
    static final String TABLE = "dextra_attempts";

    /** One day of practice for one person: counts, and the average of each number over the scored attempts. */
    public record Day(String day, int attempts, int scored, Double accuracy, Double smoothness, Double speed, Double coverage) {
    }

    private final Path file;
    private final List<AttemptRecord> records = new ArrayList<>();
    private String jdbcUrl;
    private Properties dbProps;

    @Autowired
    public AttemptLog(@Value("${dextra.data.dir:${user.home}/.dextra}") String dir,
                      @Value("${dextra.tiger.url:${TIGER_DATABASE_URL:}}") String tigerUrl) {
        this.file = dir == null || dir.isBlank() ? null : Path.of(dir, "attempts.jsonl");
        if (tigerUrl != null && !tigerUrl.isBlank()) {
            connectTiger(tigerUrl.trim());
        }
        if (jdbcUrl == null) {
            load();
        }
    }

    public AttemptLog(String dir) {
        this(dir, "");
    }

    public static AttemptLog memoryOnly() {
        return new AttemptLog("", "");
    }

    /** "Tiger Cloud" or "local file" (or "memory"), shown in clinic mode so it's clear where the history lives. */
    public String storage() {
        return jdbcUrl != null ? "Tiger Cloud" : file != null ? "local file" : "memory";
    }

    // ---------- Tiger Cloud ----------

    /** postgres://user:pass@host:port/db?sslmode=require becomes a JDBC URL plus user and password. */
    static String[] toJdbc(String url) {
        URI u = URI.create(url);
        String[] userPass = u.getRawUserInfo() == null ? new String[]{"", ""} : u.getRawUserInfo().split(":", 2);
        String query = u.getRawQuery() == null ? "" : u.getRawQuery();
        String jdbc = "jdbc:postgresql://" + u.getHost() + ":" + (u.getPort() < 0 ? 5432 : u.getPort()) + u.getRawPath()
                + "?" + (query.isEmpty() ? "" : query + "&") + "connectTimeout=10&socketTimeout=20";
        return new String[]{jdbc, URLDecoder.decode(userPass[0], StandardCharsets.UTF_8),
                userPass.length > 1 ? URLDecoder.decode(userPass[1], StandardCharsets.UTF_8) : ""};
    }

    private void connectTiger(String url) {
        try {
            String[] parts = toJdbc(url);
            Properties props = new Properties();
            props.setProperty("user", parts[1]);
            props.setProperty("password", parts[2]);
            try (Connection c = DriverManager.getConnection(parts[0], props); Statement s = c.createStatement()) {
                s.execute("CREATE TABLE IF NOT EXISTS " + TABLE + " (started_at TIMESTAMPTZ NOT NULL, id TEXT NOT NULL, "
                        + "person TEXT NOT NULL, state TEXT, scored BOOLEAN NOT NULL, refused_reason TEXT, accuracy DOUBLE PRECISION, "
                        + "smoothness DOUBLE PRECISION, speed DOUBLE PRECISION, coverage DOUBLE PRECISION, hits INT, refusals INT, fingers TEXT)");
                s.execute("SELECT create_hypertable('" + TABLE + "', 'started_at', if_not_exists => TRUE)");
                s.execute("CREATE INDEX IF NOT EXISTS " + TABLE + "_person ON " + TABLE + " (lower(person), started_at DESC)");
            }
            jdbcUrl = parts[0];
            dbProps = props;
            log.info("Saved attempts: Tiger Cloud hypertable {}", TABLE);
        } catch (Exception e) {
            log.warn("Tiger Cloud not reachable, saving attempts to the local file instead: {}", e.toString());
            jdbcUrl = null;
        }
    }

    private Connection db() throws Exception {
        return DriverManager.getConnection(jdbcUrl, dbProps);
    }

    private static Double num(ResultSet rs, String col) throws Exception {
        double v = rs.getDouble(col);
        return rs.wasNull() ? null : Math.round(v * 10) / 10.0;
    }

    // ---------- local file ----------

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

    // ---------- the API the app uses ----------

    public synchronized void append(AttemptRecord r) {
        if (jdbcUrl != null) {
            try (Connection c = db(); PreparedStatement p = c.prepareStatement("INSERT INTO " + TABLE
                    + " (started_at, id, person, state, scored, refused_reason, accuracy, smoothness, speed, coverage, hits, refusals, fingers)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                p.setTimestamp(1, Timestamp.from(Instant.parse(r.startedAt())));
                p.setString(2, r.id());
                p.setString(3, r.person());
                p.setString(4, r.state());
                p.setBoolean(5, r.scored());
                p.setString(6, r.refusedReason());
                p.setObject(7, r.accuracy());
                p.setObject(8, r.smoothness());
                p.setObject(9, r.speed());
                p.setObject(10, r.coverage());
                p.setInt(11, r.hits());
                p.setInt(12, r.refusals());
                p.setString(13, r.fingers() == null ? "" : String.join(",", r.fingers()));
                p.executeUpdate();
                return;
            } catch (Exception e) {
                log.warn("Could not save the attempt to Tiger Cloud, keeping it in the local file: {}", e.toString());
            }
        }
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
        if (jdbcUrl != null) {
            List<AttemptRecord> out = new ArrayList<>();
            try (Connection c = db(); PreparedStatement p = c.prepareStatement("SELECT * FROM " + TABLE
                    + " WHERE lower(person) = lower(?) ORDER BY started_at")) {
                p.setString(1, person);
                try (ResultSet rs = p.executeQuery()) {
                    while (rs.next()) {
                        String f = rs.getString("fingers");
                        out.add(new AttemptRecord(rs.getString("id"), rs.getString("person"),
                                rs.getTimestamp("started_at").toInstant().toString(), rs.getString("state"), rs.getBoolean("scored"),
                                rs.getString("refused_reason"), num(rs, "accuracy"), num(rs, "smoothness"), num(rs, "speed"),
                                num(rs, "coverage"), rs.getInt("hits"), rs.getInt("refusals"),
                                f == null || f.isEmpty() ? List.of() : Arrays.asList(f.split(","))));
                    }
                }
                return out;
            } catch (Exception e) {
                log.warn("Could not read from Tiger Cloud: {}", e.toString());
            }
        }
        List<AttemptRecord> out = new ArrayList<>();
        for (AttemptRecord r : records) {
            if (r.person() != null && r.person().equalsIgnoreCase(person)) {
                out.add(r);
            }
        }
        return out;
    }

    /** Practice per day (UTC): Timescale's time_bucket on Tiger Cloud, the same grouping in Java for the local file. */
    public synchronized List<Day> daily(String person) {
        if (jdbcUrl != null) {
            List<Day> out = new ArrayList<>();
            try (Connection c = db(); PreparedStatement p = c.prepareStatement(
                    "SELECT time_bucket('1 day', started_at) AS day, count(*) AS attempts, count(*) FILTER (WHERE scored) AS scored, "
                            + "avg(accuracy) FILTER (WHERE scored) AS accuracy, avg(smoothness) FILTER (WHERE scored) AS smoothness, "
                            + "avg(speed) FILTER (WHERE scored) AS speed, avg(coverage) FILTER (WHERE scored) AS coverage "
                            + "FROM " + TABLE + " WHERE lower(person) = lower(?) GROUP BY day ORDER BY day")) {
                p.setString(1, person);
                try (ResultSet rs = p.executeQuery()) {
                    while (rs.next()) {
                        out.add(new Day(rs.getTimestamp("day").toInstant().atZone(ZoneOffset.UTC).toLocalDate().toString(),
                                rs.getInt("attempts"), rs.getInt("scored"), num(rs, "accuracy"), num(rs, "smoothness"),
                                num(rs, "speed"), num(rs, "coverage")));
                    }
                }
                return out;
            } catch (Exception e) {
                log.warn("Could not read daily trends from Tiger Cloud: {}", e.toString());
            }
        }
        Map<String, List<AttemptRecord>> byDay = new TreeMap<>();
        for (AttemptRecord r : forPerson(person)) {
            String day = LocalDate.ofInstant(Instant.parse(r.startedAt()), ZoneOffset.UTC).toString();
            byDay.computeIfAbsent(day, k -> new ArrayList<>()).add(r);
        }
        List<Day> out = new ArrayList<>();
        byDay.forEach((day, rs) -> {
            List<AttemptRecord> sc = rs.stream().filter(AttemptRecord::scored).toList();
            out.add(new Day(day, rs.size(), sc.size(), avg(sc, AttemptRecord::accuracy), avg(sc, AttemptRecord::smoothness),
                    avg(sc, AttemptRecord::speed), avg(sc, AttemptRecord::coverage)));
        });
        return out;
    }

    private static Double avg(List<AttemptRecord> rs, java.util.function.Function<AttemptRecord, Double> get) {
        double[] v = rs.stream().map(get).filter(x -> x != null).mapToDouble(Double::doubleValue).toArray();
        return v.length == 0 ? null : Math.round(Arrays.stream(v).average().orElse(0) * 10) / 10.0;
    }

    /** Everyone who has practised, most recent first, with how many attempts each. */
    public synchronized List<Map<String, Object>> people() {
        if (jdbcUrl != null) {
            List<Map<String, Object>> out = new ArrayList<>();
            try (Connection c = db(); Statement s = c.createStatement(); ResultSet rs = s.executeQuery(
                    "SELECT (array_agg(person ORDER BY started_at DESC))[1] AS person, count(*) AS attempts, max(started_at) AS last "
                            + "FROM " + TABLE + " GROUP BY lower(person) ORDER BY last DESC")) {
                while (rs.next()) {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("person", rs.getString("person"));
                    m.put("attempts", rs.getInt("attempts"));
                    m.put("last", rs.getTimestamp("last").toInstant().toString());
                    out.add(m);
                }
                return out;
            } catch (Exception e) {
                log.warn("Could not list people from Tiger Cloud: {}", e.toString());
            }
        }
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
