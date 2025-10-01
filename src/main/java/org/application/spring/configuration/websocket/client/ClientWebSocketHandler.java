package org.application.spring.configuration.websocket.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.application.spring.ddd.dto.ChatMessage;
import org.springframework.web.socket.*;

import java.text.MessageFormat;

public class ClientWebSocketHandler implements WebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();


    //onOpen
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("Connection established");
        session.sendMessage(new TextMessage("Hello Server!"));
    }

    //onMessage
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        ChatMessage chatMessage = objectMapper.readValue(message.getPayload().toString(), ChatMessage.class);
        System.out.println(MessageFormat.format("{0}: {1}: {2}", chatMessage.type(), chatMessage.email(), chatMessage.message()));
    }

    //onError
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        exception.printStackTrace();
    }

    //onClose
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        System.out.println("Connection closed: " + closeStatus);
        WebSocketClientApp.connect();
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}

