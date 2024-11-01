package org.sparta.springtask.common.exception;

import jakarta.validation.ConstraintViolationException;
import org.sparta.springtask.common.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = ApiException.class)
    public ResponseEntity<?> handleApiException(ApiException apiException) {
        ErrorResponse errorResponse = ErrorResponse.from(apiException);
        return new ResponseEntity<>(errorResponse, apiException.getHttpStatus());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getAllErrors()
                .forEach(c -> errors.put(((FieldError) c).getField(), c.getDefaultMessage()));
        return ResponseEntity.badRequest()
                .body(errors);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    ResponseEntity<Map<String, String>> onConstraintValidationException(
            ConstraintViolationException e) {
        var constraintViolations = e.getConstraintViolations();
        Map<String, String> errors = new HashMap<>();
        for (final var constraint : constraintViolations) {

            String message = constraint.getMessage();
            String[] split = constraint.getPropertyPath()
                    .toString()
                    .split("\\.");
            String propertyPath = split[split.length - 1];
            errors.put(propertyPath, message);

        }
        return ResponseEntity.badRequest()
                .body(errors);
    }
}
