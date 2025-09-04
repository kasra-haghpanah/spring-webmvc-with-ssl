package org.application.spring.configuration.server;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class ServerUtil {

    public static void setCacheForBrowser(HttpServletResponse response, int expiresInHour) {
        // تنظیم کش برای 7 روز (به ثانیه)
        int cacheDurationInSeconds = expiresInHour * 3600; //7 * 24 * 60 * 60
        response.setHeader("Cache-Control", "public, max-age=" + cacheDurationInSeconds);

        // تنظیم تاریخ انقضا
        Instant expiresAt = Instant.now().plusSeconds(cacheDurationInSeconds);
        response.setHeader("Expires", DateTimeFormatter.RFC_1123_DATE_TIME
                .withZone(ZoneOffset.UTC)
                .format(expiresAt));
    }


    public static String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] buf = request.getContentAsByteArray();
        if (buf.length > 0) {
            try {
                return new String(buf, request.getCharacterEncoding());
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return "";
    }

    public static String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] buf = response.getContentAsByteArray();
        if (buf.length > 0) {
            try {
                return new String(buf, response.getCharacterEncoding());
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return "";
    }

}
