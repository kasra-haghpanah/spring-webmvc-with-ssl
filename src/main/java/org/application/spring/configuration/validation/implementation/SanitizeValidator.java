package org.application.spring.configuration.validation.implementation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.application.spring.configuration.security.XssFilterConfig;
import org.application.spring.configuration.validation.api.Sanitize;

public class SanitizeValidator implements ConstraintValidator<Sanitize, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true;
        XssFilterConfig.sanitize(value);
        return true;
    }
}

