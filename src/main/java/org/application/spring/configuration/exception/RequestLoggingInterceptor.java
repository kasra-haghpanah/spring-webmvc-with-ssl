package org.application.spring.configuration.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

/*    private final LoggingService loggingService;

    public RequestLoggingInterceptor(LoggingService loggingService) {
        this.loggingService = loggingService;
    }*/

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {
        //loggingService.logRequest(request, handler);
        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex
    ) {
        //loggingService.logResponse(request, response, handler, ex);
    }
}

