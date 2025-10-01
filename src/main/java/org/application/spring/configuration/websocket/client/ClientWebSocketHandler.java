package org.application.spring.configuration.websocket.client;

import org.springframework.web.socket.*;

public class ClientWebSocketHandler implements WebSocketHandler {

    //onOpen
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("Connection established");
        session.sendMessage(new TextMessage("Hello Server!"));
    }

    //onMessage
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        System.out.println("Received: " + message.getPayload());
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

