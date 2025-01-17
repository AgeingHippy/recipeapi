package com.ageinghippy.recipeapi.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RequiredAndNotGoofyValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredAndNotGoofy {
    String message() default "required and may not contain the word 'goofy'";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
