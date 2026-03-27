package edu.eci.arsw.blueprints.controllers;

import edu.eci.arsw.blueprints.model.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> {
            String field = err.getField();
            String message = err.getDefaultMessage();
            errors.put(field, message);
        });
        ApiResponse<Map<String, String>> body = new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
