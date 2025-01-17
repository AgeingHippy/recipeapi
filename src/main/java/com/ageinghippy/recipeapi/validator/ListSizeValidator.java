package com.ageinghippy.recipeapi.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class ListSizeValidator implements ConstraintValidator<ListSize, List<?>> {

    private int minSize;
    private int maxSize;

    @Override
    public void initialize(ListSize constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.minSize = constraintAnnotation.minSize();
        this.maxSize = constraintAnnotation.maxSize();
    }

    @Override
    public boolean isValid(List<?> value, ConstraintValidatorContext context) {
        boolean result = true;
        String customMessage = "count must be ";

        context.disableDefaultConstraintViolation();

        if (minSize != -1) {
            customMessage = customMessage + "greater than or equal to " + minSize;
            result = value != null && value.size() >= minSize;
        }
        if (maxSize != -1) {
            customMessage = customMessage + (minSize != -1 ? " and " : "") +
                    "less than or equal to " + maxSize;
            result = result && value != null && value.size() <= maxSize;
        }

        customMessageForValidation(context, customMessage);
        return result;
    }

    private void customMessageForValidation(ConstraintValidatorContext constraintContext, String message) {
        // build new violation message and add it
        constraintContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
