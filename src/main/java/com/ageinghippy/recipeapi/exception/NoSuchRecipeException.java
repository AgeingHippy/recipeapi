package com.ageinghippy.recipeapi.exception;

public class NoSuchRecipeException extends Exception {
    public NoSuchRecipeException(String message) {
        super(message);
    }

    public NoSuchRecipeException() {
    }
}
