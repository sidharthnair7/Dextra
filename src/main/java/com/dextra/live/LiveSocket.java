package com.dextra.live;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/** Pushes tracker messages to every open browser at /ws/live. New browsers get the latest status first. */
@Component
public class LiveSocket extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private volatile String lastStatus = statusJson("starting");

    static String statusJson(String msg) {
        return "{\"type\":\"status\",\"msg\":\"" + msg.replace("\\", "\\\\").replace("\"", "\\\"") + "\"}";
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        send(session, lastStatus);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    public void status(String msg) {
        lastStatus = statusJson(msg);
        broadcast(lastStatus);
    }

    public void broadcast(String json) {
        for (WebSocketSession s : sessions) {
            send(s, json);
        }
    }

    private void send(WebSocketSession s, String json) {
        if (!s.isOpen()) {
            sessions.remove(s);
            return;
        }
        try {
            synchronized (s) {
                s.sendMessage(new TextMessage(json));
            }
        } catch (Exception e) {
            sessions.remove(s);
        }
    }
}
