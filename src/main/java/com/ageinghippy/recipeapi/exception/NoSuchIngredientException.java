package com.ageinghippy.recipeapi.exception;

public class NoSuchIngredientException extends Exception{
    public NoSuchIngredientException(String message) {
        super(message);
    }

    public NoSuchIngredientException() {
    }
}
