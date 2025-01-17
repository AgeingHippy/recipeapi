package com.ageinghippy.recipeapi.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ListSizeValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ListSize {
    String message() default "size is invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int minSize() default -1;
    int maxSize() default -1;
}
