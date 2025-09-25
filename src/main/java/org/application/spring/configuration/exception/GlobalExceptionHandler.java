package org.application.spring.configuration.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.LocaleResolver;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {


    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;

    public GlobalExceptionHandler(MessageSource messageSource, LocaleResolver localeResolver) {
        this.messageSource = messageSource;
        this.localeResolver = localeResolver;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true) {
            @Override
            public void setAsText(String text) {
                String sanitized = Jsoup.clean(text, Safelist.basic());
                super.setAsText(sanitized);
            }
        });
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        Locale locale = localeResolver.resolveLocale(request);
        request.setAttribute("loggedException", ex);

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            String localizedMessage = messageSource.getMessage(error.getDefaultMessage(), new Object[]{error.getCodes()[0]}, locale);
            errors.put(error.getField(), localizedMessage);
        }

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(HttpStatus.BAD_REQUEST);
        errorResponse.setErrors(errors);
        return ResponseEntity.badRequest().header("Content-Type", "application/json;charset=UTF-8").body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        Locale locale = localeResolver.resolveLocale(request);
        request.setAttribute("loggedException", ex);

        for (ConstraintViolation error : ex.getConstraintViolations()) {
            Map<String, Object> attributes = error.getConstraintDescriptor().getAttributes();
            Path path = error.getPropertyPath();
            Object invalidValue = error.getInvalidValue();
            String localizedMessage = error.getMessage();
            try {
                localizedMessage = messageSource.getMessage(error.getMessage(), new Object[]{path}, locale);
            } catch (Exception e) {

            }
            errors.put(error.getMessageTemplate(), localizedMessage);
        }

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(HttpStatus.BAD_REQUEST);
        errorResponse.setErrors(errors);
        return ResponseEntity.badRequest().header("Content-Type", "application/json;charset=UTF-8").body(errorResponse);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(ApplicationException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        Locale locale = localeResolver.resolveLocale(request);
        request.setAttribute("loggedException", ex);

        String message = messageSource.getMessage(ex.getMessage(), ex.getVariables(), locale);
        errors.put(ex.getMessage(), message);

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(ex.getStatusCode());
        errorResponse.setErrors(errors);
        return ResponseEntity.status(ex.getStatusCode()).header("Content-Type", "application/json;charset=UTF-8").body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();
        Locale locale = localeResolver.resolveLocale(request);
        request.setAttribute("loggedException", ex);

        String message = messageSource.getMessage("error.unexpected", null, locale);
        errors.put("unexpected", message);

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        errorResponse.setErrors(errors);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header("Content-Type", "application/json;charset=UTF-8")
                .body(errorResponse);
    }

}

