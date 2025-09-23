package org.application.spring.configuration.validation.api;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.application.spring.configuration.validation.implementation.ListSizeValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ListSizeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ListSize {
    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int min();

    int max();
}

