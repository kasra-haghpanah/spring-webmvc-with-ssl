package org.application.spring.configuration.validation.implementation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.application.spring.configuration.validation.api.ListSize;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListSizeValidator implements ConstraintValidator<ListSize, List<?>> {
    private int min;
    private int max;
    private String regx;

    @Override
    public void initialize(ListSize constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
        this.regx = constraintAnnotation.regx();
    }

    @Override
    public boolean isValid(List<?> list, ConstraintValidatorContext context) {
        if (list == null) return false;
        int size = list.size();
        if (size < min) {
            return false;
        }
        if (size > max) {
            return false;
        }

        if (!regx.equals("")) {
            for (Object node : list) {
                if (node == null) {
                    return false;
                }
                if (node instanceof String) {
                    String input = (String) node;
                    boolean isValid = input.matches(regx);
                    if (!isValid) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}


