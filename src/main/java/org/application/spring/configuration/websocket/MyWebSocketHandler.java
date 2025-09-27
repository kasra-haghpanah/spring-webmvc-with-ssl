package org.application.spring.configuration.websocket;

import io.jsonwebtoken.Claims;
import org.application.spring.configuration.security.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class MyWebSocketHandler extends TextWebSocketHandler {

    // onOpen
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        String token = (String) session.getAttributes().get("jwt");

        if (token == null || token.equals("")) {
            token = UriComponentsBuilder.fromUri(session.getUri()).build().getQueryParams().getFirst("token");
        }

        try {
            Claims claims = JwtUtil.extractAllClaims(token);
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
        session.sendMessage(new TextMessage("Echo: " + payload));
    }

    // onClose
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // اتصال بسته شد
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

