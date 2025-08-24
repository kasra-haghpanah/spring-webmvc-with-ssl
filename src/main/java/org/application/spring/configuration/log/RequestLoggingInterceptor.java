package org.application.spring.configuration.log;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.UTF8JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.logstash.logback.argument.StructuredArgument;
import net.logstash.logback.argument.StructuredArguments;
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
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    public static <T> String toJson(T t){
        try {
            return JSON_MAPPER.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


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

        if (ex == null) {
            ex = (Exception) request.getAttribute("loggedException");
            request.removeAttribute("loggedException");
        }

        Long duration = (Long) request.getAttribute("start-time");
        request.removeAttribute("start-time");
        if (duration != null) {
            duration = (System.nanoTime() - duration) / 1_000_000;
        }

        LogstashHttpLog log = new LogstashHttpLog(
                ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                duration,
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                extractHeaders(request),
                extractRequestBody(request),
                extractHeaders(response),
                extractResponseBody(response, request),
                response.getStatus(),
                ex != null ? ex.getClass().getSimpleName() : null,
                ex != null ? getStackTrace(ex) : null
        );


/*        try {
            StructuredArgument arg = StructuredArguments.f(log);

            StringWriter writer = new StringWriter();
            UTF8JsonGenerator utf8JsonGenerator = new UTF8JsonGenerator();
            JsonGenerator generator = new JsonFactory().createGenerator(writer);
            generator.setCodec(new com.fasterxml.jackson.databind.ObjectMapper());
            StructuredArguments.f(log).writeTo(generator);
            logger.info(generator.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/

        String json = RequestLoggingInterceptor.<LogstashHttpLog>toJson(log);
        logger.info(json);

    }

    private Map<String, String> extractHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            headers.put(name, request.getHeader(name));
        }
        return headers;
    }

    private Map<String, String> extractHeaders(HttpServletResponse response) {
        Map<String, String> headers = new HashMap<>();
        for (String name : response.getHeaderNames()) {
            headers.put(name, response.getHeader(name));
        }
        return headers;
    }

    private String extractRequestBody(HttpServletRequest request) {

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

    private String extractResponseBody(HttpServletResponse response, HttpServletRequest request) {

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

    private String getStackTrace(Exception ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }


}

