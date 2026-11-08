package com.dextra.live;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Runs the real app with the camera off and checks the contracts the frontend relies on. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "dextra.camera.enabled=false")
class LiveLoopTest {

    @LocalServerPort
    int port;

    @Autowired
    LiveSocket socket;

    @Test
    void pointJsonMatchesTheFrontendContract() {
        assertEquals("{\"type\":\"point\",\"t\":120,\"ok\":true,\"x\":0.5123,\"y\":0.4310,\"c\":0.981,\"fps\":29.6}",
                new TrackPoint(120, true, 0.51234, 0.43101, 0.9812, 29.62).toJson());
        assertEquals("{\"type\":\"point\",\"t\":150,\"ok\":false,\"fps\":29.4}",
                new TrackPoint(150, false, 0, 0, 0, 29.4).toJson());
    }

    @Test
    void statusTextIsEscaped() {
        assertEquals("{\"type\":\"status\",\"msg\":\"say \\\"hi\\\"\"}", LiveSocket.statusJson("say \"hi\""));
    }

    @Test
    void browserGetsStatusOnConnectThenPoints() throws Exception {
        BlockingQueue<String> got = new LinkedBlockingQueue<>();
        WebSocketSession s = new StandardWebSocketClient().execute(new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) {
                got.add(message.getPayload());
            }
        }, "ws://localhost:" + port + "/ws/live").get(5, TimeUnit.SECONDS);

        assertEquals("{\"type\":\"status\",\"msg\":\"camera disabled\"}", got.poll(5, TimeUnit.SECONDS));
        String point = new TrackPoint(5, true, 0.25, 0.75, 0.9, 30).toJson();
        socket.broadcast(point);
        assertEquals(point, got.poll(5, TimeUnit.SECONDS));
        s.close();
    }

    @Test
    void statusEndpointAnswers() throws Exception {
        HttpResponse<String> r = HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/status")).build(),
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, r.statusCode());
        assertTrue(r.body().contains("\"status\":\"camera disabled\""), r.body());
    }
}
