package org.application.spring.configuration.server;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.application.spring.configuration.properties.Properties;
import org.application.spring.configuration.security.SecurityConfig;
import org.springframework.context.annotation.Configuration;

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

        String path = "";
        if (httpRequest.getRequestURI().startsWith(contextPath + "/")) {
            path = contextPath;
        }

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletRequest wrappedRequest = new ContextPathAndXssRequestWrapper(req, path);
        chain.doFilter(wrappedRequest, response);
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }

    private static class ContextPathAndXssRequestWrapper extends HttpServletRequestWrapper {
        private final String contextPath;

        public ContextPathAndXssRequestWrapper(HttpServletRequest request, String contextPath) {
            super(request);
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
