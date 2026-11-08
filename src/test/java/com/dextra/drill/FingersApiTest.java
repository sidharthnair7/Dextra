package com.dextra.drill;

import com.dextra.live.TrackPoint;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The finger picker's API on the real app (camera off), plus the lock and the fingertip JSON it relies on. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"dextra.camera.enabled=false", "dextra.gemini.api-key="})
class FingersApiTest {

    @LocalServerPort
    int port;

    private final HttpClient http = HttpClient.newHttpClient();

    private HttpResponse<String> post(String path, String json) throws Exception {
        return http.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json)).build(),
                HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> get(String path) throws Exception {
        return http.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).GET().build(),
                HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void tapPickingTwoFingersThroughTheApi() throws Exception {
        HttpResponse<String> ok = post("/api/fingers", "{\"fingers\":[\"middle\",\"thumb\"]}");
        assertEquals(200, ok.statusCode(), ok.body());
        assertEquals("{\"fingers\":[\"thumb\",\"middle\"],\"lockedBy\":\"tap\"}", ok.body());
        assertEquals(ok.body(), get("/api/fingers").body());

        HttpResponse<String> same = post("/api/fingers", "{\"fingers\":[\"index\",\"index\"]}");
        assertEquals(400, same.statusCode());
        assertTrue(same.body().contains("pick two different fingers"), same.body());

        HttpResponse<String> unknown = post("/api/fingers", "{\"fingers\":[\"thumb\",\"claw\"]}");
        assertEquals(400, unknown.statusCode());
        assertTrue(unknown.body().contains("unknown finger"), unknown.body());

        HttpResponse<String> one = post("/api/fingers", "{\"fingers\":[\"thumb\"]}");
        assertEquals(400, one.statusCode());
        assertTrue(one.body().contains("pick exactly two fingers"), one.body());

        assertTrue(post("/api/fingers/listen", "").body().contains("\"listening\":true"));
        assertTrue(post("/api/fingers/listen/stop", "").body().contains("\"listening\":false"));

        assertEquals(200, post("/api/fingers", "{\"fingers\":[\"thumb\",\"index\"]}").statusCode());   // leave the default
    }

    @Test
    void lockDefaultsToThumbAndIndexAndKeepsTheLowerFingerFirst() {
        FingerLock lock = new FingerLock();
        assertEquals("{\"type\":\"fingersLocked\",\"fingers\":[\"thumb\",\"index\"],\"lockedBy\":\"default\"}",
                lock.json("fingersLocked"));
        lock.set("Pinky", "ring", "tap");
        assertArrayEquals(new int[]{3, 4}, lock.pair());
        assertThrows(IllegalArgumentException.class, () -> lock.set(2, 2, "tap"));
    }

    @Test
    void pointMessageCarriesAllFiveFingertipsAndThePalm() {
        double[] tips = {0.4, 0.5, 0.42, 0.5, 0.6, 0.2, 0.7, 0.24, 0.8, 0.3};
        String json = new TrackPoint(5, true, 0.42, 0.5, 0.9, 30, tips, 0.1).toJson();
        assertTrue(json.startsWith("{\"type\":\"point\",\"t\":5,\"ok\":true,\"x\":0.4200,\"y\":0.5000,"), json);
        assertTrue(json.endsWith(",\"tips\":[[0.4000,0.5000],[0.4200,0.5000],[0.6000,0.2000],[0.7000,0.2400],[0.8000,0.3000]],\"palm\":0.1000}"), json);
        assertEquals("{\"type\":\"pinch\",\"fingers\":[\"thumb\",\"index\"],\"count\":2,\"of\":3}",
                PinchLockService.progressJson(new PinchDetector.Progress(0, 1, 2)));
    }

    @Test
    void theInstrumentGoesBetweenTheLockedFingersAndTheGapWorksTheJaws() {
        double[] tips = {0.40, 0.50, 0.50, 0.50, 0.60, 0.20, 0.70, 0.24, 0.80, 0.30};
        FingerLock lock = new FingerLock();                     // thumb + index
        FingerLock.Steer s = lock.steer(tips, 0.10, 0.75);
        assertEquals(0.45, s.x(), 1e-9);
        assertEquals(0.50, s.y(), 1e-9);
        assertEquals(1.0, s.gap(), 1e-9);                       // 0.1 apart = one palm width

        lock.set("thumb", "middle", "tap");                     // someone without an index finger
        s = lock.steer(tips, 0.10, 0.75);
        assertEquals(0.50, s.x(), 1e-9);
        assertEquals(0.35, s.y(), 1e-9);
        assertEquals(Math.hypot(0.20, 0.30 * 0.75) / 0.10, s.gap(), 1e-9);   // y counts at the frame's aspect

        assertEquals(-1, lock.steer(tips, 0, 0.75).gap(), 1e-9);  // no palm width, no gap

        String json = new TrackPoint(5, true, 0.45, 0.5, 0.9, 30, tips, 0.1, 1.0).toJson();
        assertTrue(json.endsWith(",\"palm\":0.1000,\"gap\":1.000}"), json);
    }
}
