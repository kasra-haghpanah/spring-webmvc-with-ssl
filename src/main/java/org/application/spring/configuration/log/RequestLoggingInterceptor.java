package org.application.spring.configuration.log;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.logstash.logback.argument.StructuredArguments;
import org.application.spring.configuration.server.InvalidTokenType;
import org.application.spring.configuration.server.ServerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(request instanceof ContentCachingRequestWrapper)) {
            request = new ContentCachingRequestWrapper(request, 4_096);
        }
        if (!(response instanceof ContentCachingResponseWrapper)) {
            response = new ContentCachingResponseWrapper(response);
        }
        return true;
    }


    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex
    ) {
        setLog(request, response, handler, ex);
    }

    public static void setLog(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex
    ) {
        if (ex == null) {
            ex = (Exception) request.getAttribute("loggedException");
            request.removeAttribute("loggedException");
        }

        Long duration = (Long) request.getAttribute("start-time");
        request.removeAttribute("start-time");
        if (duration != null) {
            duration = (System.nanoTime() - duration) / 1_000_000;
        }

        InvalidTokenType tokenType = InvalidTokenType.NONE;

        tokenType = (InvalidTokenType) request.getAttribute("invalidTokenType");
        request.removeAttribute("invalidTokenType");


        String tokenValue = (String) request.getAttribute("tokenValue");
        request.removeAttribute("tokenValue");

        if (tokenValue == null || tokenValue.trim().equals("")) {
            tokenValue = ServerUtil.getAuthorization(request);
        }

        LogstashHttpLog log = new LogstashHttpLog(
                request.getRemoteAddr(),
                tokenType,
                tokenValue,
                ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                duration,
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                extractHeaders(request),
                extractRequestBody(request),
                extractHeaders(response),
                extractResponseBody(response, request),
                (response != null) ? response.getStatus() : 0,
                ex != null ? ex.getClass().getSimpleName() : null,
                ex != null ? getStackTrace(ex) : null
        );

        logger.info("Request completed!", StructuredArguments.f(log));
    }

    public static Map<String, String> extractHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            headers.put(name, request.getHeader(name));
        }
        return headers;
    }

    public static Map<String, String> extractHeaders(HttpServletResponse response) {
        Map<String, String> headers = new HashMap<>();
        if (response == null) {
            return headers;
        }
        for (String name : response.getHeaderNames()) {
            headers.put(name, response.getHeader(name));
        }
        return headers;
    }

    public static String extractRequestBody(HttpServletRequest request) {

        String requestBody = (String) request.getAttribute("request-body");

        if (requestBody != null && !requestBody.equals("")) {
            request.removeAttribute("request-body");
            return requestBody;
        }

        ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            try {
                return new String(buf, wrapper.getCharacterEncoding());
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static String extractResponseBody(HttpServletResponse response, HttpServletRequest request) {

        String responseBody = (String) request.getAttribute("response-body");

        if (responseBody != null && !responseBody.equals("")) {
            request.removeAttribute("response-body");
            return responseBody;
        }

        ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            String body = null;
            try {
                body = new String(buf, wrapper.getCharacterEncoding());
                wrapper.copyBodyToResponse(); // مهم: برای ارسال پاسخ واقعی
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return body;
        }
        return null;
    }

    public static String getStackTrace(Exception ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }


}

