package com.dextra.drill;

import com.dextra.live.TrackPoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The REST contract the frontend codes against, on the real app with the camera off. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"dextra.camera.enabled=false", "dextra.gemini.api-key=", "dextra.data.dir="})
class DrillApiTest {

    @LocalServerPort
    int port;

    @Autowired
    AttemptService attempts;

    private final HttpClient http = HttpClient.newHttpClient();

    private HttpResponse<String> call(String method, String path) throws Exception {
        return http.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .method(method, HttpRequest.BodyPublishers.noBody()).build(), HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void fullAttemptThroughTheApi() throws Exception {
        HttpResponse<String> start = call("POST", "/api/attempts/start");
        assertEquals(200, start.statusCode());
        String body = start.body();
        assertTrue(body.contains("\"attemptId\""), body);
        assertTrue(body.contains("\"targets\":[{\"id\":1,\"x\":0.2,\"y\":0.25,\"r\":0.04}"), body);
        String id = body.replaceAll(".*\"attemptId\":\"([^\"]+)\".*", "$1");

        for (TrackPoint p : new SyntheticPaths().build(AttemptService.course())) {
            attempts.onPoint(p);
        }

        HttpResponse<String> got = call("GET", "/api/attempts/" + id);
        assertEquals(200, got.statusCode());
        String json = got.body();
        for (String field : new String[]{"\"state\":\"done\"", "\"scored\":true", "\"points\":[", "\"hits\":[",
                "\"accuracy\":{\"value\":", "\"smoothness\":{", "\"speed\":{", "\"coverage\":{", "\"formula\":",
                "\"detail\":", "\"refusals\":[", "\"feedback\":"}) {
            assertTrue(json.contains(field), "missing " + field);
        }

        // The coach runs on its own thread; with no key it's the offline coach, so it lands within a moment.
        String withFeedback = json;
        for (int i = 0; i < 50 && !withFeedback.contains("\"feedback\":\"Offline coach:"); i++) {
            Thread.sleep(20);
            withFeedback = call("GET", "/api/attempts/" + id).body();
        }
        assertTrue(withFeedback.contains("\"feedback\":\"Offline coach:"), withFeedback);

        String list = call("GET", "/api/attempts").body();
        assertTrue(list.startsWith("[{\"attemptId\":\"" + id + "\""), list);
        assertEquals(404, call("GET", "/api/attempts/nope").statusCode());
    }

    private HttpResponse<String> postJson(String path, String json) throws Exception {
        return http.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json)).build(),
                HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void progressReviewThroughTheApi() throws Exception {
        String person = "Test Person " + System.nanoTime();
        assertEquals(200, postJson("/api/attempts/start", "{\"person\":\"" + person + "\"}").statusCode());
        for (TrackPoint p : new SyntheticPaths().build(AttemptService.course())) {
            attempts.onPoint(p);
        }

        HttpResponse<String> progress = postJson("/api/progress", "{\"person\":\"" + person.toLowerCase() + "\"}");
        assertEquals(200, progress.statusCode());
        String body = progress.body();
        assertTrue(body.contains("\"attempts\":1"), body);
        assertTrue(body.contains("\"scored\":1"), body);
        assertTrue(body.contains("\"refused\":0"), body);
        assertTrue(body.contains("\"key\":\"accuracy\""), body);
        assertTrue(body.contains("\"fingers\":[\""), body);
        assertTrue(call("GET", "/api/people").body().contains(person), "people list");
    }

    @Test
    void courseIsServedAndMatchesTheDrill() throws Exception {
        HttpResponse<String> r = call("GET", "/api/course");
        assertEquals(200, r.statusCode());
        assertTrue(r.body().startsWith("[{\"id\":1,\"x\":0.2,\"y\":0.25,\"r\":0.04}"), r.body());
        assertTrue(r.body().contains("{\"id\":5,\"x\":0.8,\"y\":0.65,\"r\":0.04}]"), r.body());
    }

    @Test
    void cameraPreviewSays503WhenTheCameraIsOff() throws Exception {
        assertEquals(503, call("GET", "/api/camera.mjpg").statusCode());
    }

    @Test
    void lightCheckWithNoCameraFailsWithAReason() throws Exception {
        HttpResponse<String> r = call("POST", "/api/check?seconds=0.2");
        assertEquals(200, r.statusCode());
        assertTrue(r.body().contains("\"pass\":false"), r.body());
        assertTrue(r.body().contains("no frames from the camera"), r.body());
        assertTrue(r.body().contains("\"advice\":\"Offline check: No frames from the camera"), r.body());
        assertTrue(r.body().contains("\"geminiSawFrame\":false"), r.body());
    }
}
