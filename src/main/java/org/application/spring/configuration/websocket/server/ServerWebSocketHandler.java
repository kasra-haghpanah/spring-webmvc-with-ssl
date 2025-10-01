package org.application.spring.configuration.websocket.server;

import io.jsonwebtoken.Claims;
import org.application.spring.configuration.security.JwtUtil;
import org.application.spring.ddd.dto.ChatMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class ServerWebSocketHandler extends TextWebSocketHandler {

    public static final Map<String, WebSocketSession> sessions = new HashMap<>();

    // onOpen
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        Claims claims = (Claims) session.getAttributes().get("identity");


        try {
            if (claims == null) {
                String token = UriComponentsBuilder.fromUri(session.getUri()).build().getQueryParams().getFirst("token");
                claims = JwtUtil.extractAllClaims(token);
            }

            session.getAttributes().put("identity", claims);
            sessions.put(session.getId(), session);


        } catch (Exception e) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Invalid JWT"));
            return;
        }

    }

    // onMessage
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // پیام دریافت شد
        String payload = message.getPayload();
        String userName = getProperty(session, "sub");
        sessions
                .entrySet()
                .stream()

                .forEach(wsSession -> {
                    try {
                        String type = "receiver";
                        if (userName.equals(getProperty(wsSession.getValue(), "sub"))) {
                            type = "sender";
                        }
                        String chat = new ChatMessage(userName, type, payload).toString();
                        wsSession.getValue().sendMessage(new TextMessage(chat));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public static <T> T getProperty(WebSocketSession session, String key) {
        Claims claims = (Claims) session.getAttributes().get("identity");
        return (T) claims.get(key);
    }

    // onClose
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // اتصال بسته شد
        sessions.remove(session.getId());
    }

    // onError
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("⚠️ خطای انتقال در WebSocket: " + exception.getMessage());

        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR.withReason("Transport error"));
        }
    }

}

