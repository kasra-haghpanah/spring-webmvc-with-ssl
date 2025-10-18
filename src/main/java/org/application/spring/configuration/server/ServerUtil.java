package org.application.spring.configuration.server;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class ServerUtil {

    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            // ممکنه چند IP وجود داشته باشه، اولی IP واقعی کاربره
            return ip.split(",")[0].trim();
        }
        ip = request.getHeader("Proxy-Client-IP");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("WL-Proxy-Client-IP");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr(); // fallback: IP مستقیم از request
    }

    public static String getToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
                .map((authorization) -> {
                    return authorization.trim().replace("Bearer ", "");
                })
                .orElseGet(() -> Optional.ofNullable(request.getCookies())
                        .map(Arrays::stream)
                        .orElseGet(Stream::empty)
                        .filter(cookie -> cookie.getName().equals("access_token") && !cookie.getValue().equals(""))
                        .findFirst()
                        .map(Cookie::getValue)
                        .map(token -> {
                            return token.trim();
                        })
                        .orElse("")
                );
    }

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
