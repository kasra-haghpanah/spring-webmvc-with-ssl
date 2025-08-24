package org.application.spring.configuration.validation.api;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.application.spring.configuration.validation.implementation.SanitizeValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SanitizeValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Sanitize {
    String message() default "ایمیل وارد شده معتبر نیست";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

