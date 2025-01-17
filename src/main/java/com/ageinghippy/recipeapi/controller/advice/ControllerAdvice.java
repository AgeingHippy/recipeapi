package com.ageinghippy.recipeapi.controller.advice;

import com.ageinghippy.recipeapi.exception.ResponseErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseErrorMessage validationFailed(MethodArgumentNotValidException e) {
        List<String> errorMessages = new ArrayList<>();
        if (!e.getAllErrors().isEmpty()) {
            e.getAllErrors().forEach(err -> {
                        String errorMessage = ((FieldError) err).getField() + " - " + err.getDefaultMessage();
                        errorMessages.add(errorMessage);
                    }
            );
        } else {
            errorMessages.add(e.getMessage());
        }

        errorMessages.sort(String::compareTo);

        return new ResponseErrorMessage(HttpStatus.BAD_REQUEST, errorMessages);
    }
}
