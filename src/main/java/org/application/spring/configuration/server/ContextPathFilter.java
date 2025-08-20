package org.application.spring.configuration.server;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.application.spring.configuration.properties.Properties;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.text.MessageFormat;

@Configuration
public class ContextPathFilter implements Filter {

    private String contextPath;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.contextPath = MessageFormat.format("/{0}", Properties.getApplicationName());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if (httpRequest.getRequestURI().startsWith(contextPath + "/")) {
            chain.doFilter(new ContextPathRequestWrapper(httpRequest, contextPath), response);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }

    private static class ContextPathRequestWrapper extends HttpServletRequestWrapper {
        private final String contextPath;

        public ContextPathRequestWrapper(HttpServletRequest request, String contextPath) {
            super(request);
            this.contextPath = contextPath;
        }

        @Override
        public String getContextPath() {
            return this.contextPath;
        }
    }
}
