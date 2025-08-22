package org.application.spring.configuration.log;

import java.util.Map;

public record LogstashHttpLog(
        String timestamp,
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

