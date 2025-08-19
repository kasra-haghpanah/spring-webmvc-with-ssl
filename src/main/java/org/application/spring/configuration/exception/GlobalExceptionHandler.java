package org.application.spring.configuration.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.LocaleResolver;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {


    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;

    public GlobalExceptionHandler(MessageSource messageSource, LocaleResolver localeResolver) {
       // ((ResourceBundleMessageSource) messageSource).setDefaultEncoding("UTF-8");
        this.messageSource = messageSource;
        this.localeResolver = localeResolver;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        Locale locale = localeResolver.resolveLocale(request);

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {

            String localizedMessage = messageSource.getMessage(error.getDefaultMessage(), new Object[]{error.getField()}, locale);
            errors.put(error.getField(), localizedMessage);

        }

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(400);
        errorResponse.setErrors(errors);
        return ResponseEntity.badRequest().header("Content-Type", "application/json;charset=UTF-8").body(errorResponse);
    }
}

