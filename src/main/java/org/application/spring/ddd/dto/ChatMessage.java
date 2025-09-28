package org.application.spring.ddd.dto;

import java.text.MessageFormat;

public record ChatMessage(String email, String type, String message) {

    @Override
    public String toString() {
        return String.format("{\"email\":\"%s\",\"type\":\"%s\",\"message\":\"%s\"}", email, type, message);
    }
}
