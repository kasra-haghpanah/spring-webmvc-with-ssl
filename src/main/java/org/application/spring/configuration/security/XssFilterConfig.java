package org.application.spring.configuration.security;

import org.application.spring.configuration.server.ContextPathAndXssFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class XssFilterConfig {
    @Bean
    public FilterRegistrationBean<ContextPathAndXssFilter> xssFilterRegistration() {
        FilterRegistrationBean<ContextPathAndXssFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ContextPathAndXssFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }
}
