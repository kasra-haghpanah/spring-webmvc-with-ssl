package org.application.spring.configuration.server;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.application.spring.configuration.exception.ApplicationException;
import org.application.spring.configuration.properties.Properties;
//import org.jsoup.Jsoup;
//import org.jsoup.safety.Safelist;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;

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

        if (httpRequest.getRequestURI().startsWith(contextPath + "/")) {
            try {
                chain.doFilter(new ContextPathAndXssRequestWrapper(httpRequest, contextPath), response);
            } catch (Exception ex) {
                throw new ApplicationException(ex.getMessage(), 500, new Object[]{});
                //request.setAttribute("loggedException", ex);
            }

        } else {
            try {
                chain.doFilter(request, response);
            } catch (Exception ex) {
                throw new ApplicationException(ex.getMessage(), 500, new Object[]{});
                //request.setAttribute("loggedException", ex);
            }
        }
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
            return sanitize(super.getParameter(name));
        }

        @Override
        public String[] getParameterValues(String name) {
            String[] values = super.getParameterValues(name);
            if (values == null) return null;

            return Arrays.stream(values)
                    .map(this::sanitize)
                    .toArray(String[]::new);
        }

        @Override
        public String getHeader(String name) {
            return sanitize(super.getHeader(name));
        }

        private String sanitize(String input) {
            return input == null ? null : Jsoup.clean(input, Safelist.basic());
        }


    }


}
