package org.application.spring.configuration.validation.implementation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.application.spring.configuration.validation.api.ListSize;

import java.util.List;

public class ListSizeValidator implements ConstraintValidator<ListSize, List<?>> {
    private int min;
    private int max;

    @Override
    public void initialize(ListSize constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(List<?> files, ConstraintValidatorContext context) {
        if (files == null) return false;
        int size = files.size();
        return size >= min && size <= max;
    }
}


