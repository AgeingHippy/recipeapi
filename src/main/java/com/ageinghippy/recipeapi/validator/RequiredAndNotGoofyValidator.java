package com.ageinghippy.recipeapi.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RequiredAndNotGoofyValidator implements ConstraintValidator<RequiredAndNotGoofy,String> {
    @Override
    public void initialize(RequiredAndNotGoofy constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && !value.isBlank() && !value.toLowerCase().contains("goofy");
    }
}
