package com.ageinghippy.recipeapi.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseErrorMessage {
    private HttpStatus statusCode;
    private List<String> errorMessages;
}
