package com.easybudget.easybudget_spring.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // JSON parse error/Deserialization errors are handled gently and automatically
    // Meaning: gentle WARN in Spring, and 400 Bad request with error message to
    // HTTP response (HttpMessageNotReadableException)
    // These errors include:
    // 1. Required Argument:String => Input:Integer (Wrong Data Types)
    // 2. When connected with other models: ID that doesn't exist

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // ConstraintViolationException are not handled automatically and require manual
    // handling
    // These errors include:
    // 1. Being Null: Not including required field/Argument
    // 2. Voilating Constraint: Lower than Minimum
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(propertyPath, message);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
        String error = ex.getBindingResult().getFieldError().getDefaultMessage();
        // List<String> errors = new ArrayList<>();
        // getFieldErrors().forEach(error ->
        //     errors.add(error.getDefaultMessage())
        // );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NonTransientAiException.class)
    public ResponseEntity<String> handleNonTransientAiException(NonTransientAiException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.TOO_MANY_REQUESTS);
    }

}
