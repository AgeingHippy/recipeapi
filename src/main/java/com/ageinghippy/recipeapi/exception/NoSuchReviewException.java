package com.ageinghippy.recipeapi.exception;

public class NoSuchReviewException extends Exception {
    public NoSuchReviewException(String message) {
        super(message);
    }

    public NoSuchReviewException() {
    }
}
