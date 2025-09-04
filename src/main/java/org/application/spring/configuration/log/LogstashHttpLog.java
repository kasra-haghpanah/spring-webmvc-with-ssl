package org.application.spring.configuration.log;

import org.application.spring.configuration.server.InvalidTokenType;

import java.util.Map;

public record LogstashHttpLog(
        String userIp,
        InvalidTokenType tokenType,
        String jwtToken,
        String timestamp,
        Long duration,
        String method,
        String path,
        String queryString,
        Map<String, String> requestHeaders,
        String requestBody,
        Map<String, String> responseHeaders,
        String responseBody,
        int status,
        String exception,
        String stackTrace
) {
}

