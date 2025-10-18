package org.application.spring.configuration.server;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.application.spring.configuration.properties.Properties;
import org.application.spring.configuration.security.SecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.text.MessageFormat;

@Configuration
public class ContextPathAndXssFilter implements Filter {

    private String contextPath;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.contextPath = MessageFormat.format("/{0}", Properties.getApplicationName());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(httpRequest, 4_096);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper((HttpServletResponse) response);

        wrappedResponse.setHeader("server", "Kasra-Haghpanah");
        request.setAttribute("request-body", ServerUtil.getRequestBody(wrappedRequest));
        request.setAttribute("response-body", ServerUtil.getResponseBody(wrappedResponse));

        String path = "";
        if (httpRequest.getRequestURI().startsWith(contextPath + "/")) {
            path = contextPath;
        } else {
            // هدایت به مسیر با contextPath
            String correctedURI = contextPath + httpRequest.getRequestURI();
            ((HttpServletResponse) response).sendRedirect(correctedURI);
            return;
        }

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        //res.setHeader("Permissions-Policy", "geolocation=(), camera=(), microphone=()");
        HttpServletRequest xssWrappedRequest = new ContextPathAndXssRequestWrapper(req, path);
        chain.doFilter(xssWrappedRequest, response);
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }

    private static class ContextPathAndXssRequestWrapper extends ContentCachingRequestWrapper {
        private final String contextPath;

        public ContextPathAndXssRequestWrapper(HttpServletRequest request, String contextPath) {
            super(request, 4_096);
            this.contextPath = contextPath;
        }

        @Override
        public String getContextPath() {
            return this.contextPath;
        }

        @Override
        public String getParameter(String name) {
            String value = super.getParameter(name);
            return SecurityConfig.sanitize(value);
        }

        @Override
        public String[] getParameterValues(String name) {
            String[] values = super.getParameterValues(name);
            if (values == null) return null;
            String[] sanitized = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                sanitized[i] = SecurityConfig.sanitize(values[i]);
            }
            return sanitized;
        }

        @Override
        public String getHeader(String name) {
            return SecurityConfig.sanitize(super.getHeader(name));
        }


    }


}
