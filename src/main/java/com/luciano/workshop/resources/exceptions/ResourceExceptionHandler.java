package com.luciano.workshop.resources.exceptions;

import com.luciano.workshop.services.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class ResourceExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> notFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return response(HttpStatus.NOT_FOUND, ex.getMessage(), request, List.of());
    }
    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<StandardError> conflict(DatabaseException ex, HttpServletRequest request) {
        return response(HttpStatus.CONFLICT, ex.getMessage(), request, List.of());
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardError> integrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        return response(HttpStatus.CONFLICT, "Operation conflicts with existing data or related records", request, List.of());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> validation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> new StandardError.FieldError(error.getField(), error.getDefaultMessage())).toList();
        return response(HttpStatus.BAD_REQUEST, "Invalid request fields", request, errors);
    }
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<StandardError> badRequest(Exception ex, HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, "Invalid request body or parameter", request, List.of());
    }
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<StandardError> methodValidation(HandlerMethodValidationException ex,
                                                          HttpServletRequest request) {
        var errors = new java.util.ArrayList<StandardError.FieldError>();
        for (var result : ex.getParameterValidationResults()) {
            if (result instanceof org.springframework.validation.method.ParameterErrors fields) {
                for (var field : fields.getFieldErrors()) {
                    errors.add(new StandardError.FieldError(field.getField(), field.getDefaultMessage()));
                }
            } else {
                String name = result.getMethodParameter().getParameterName();
                for (var error : result.getResolvableErrors()) {
                    errors.add(new StandardError.FieldError(name == null ? "parameter" : name,
                        error.getDefaultMessage()));
                }
            }
        }
        return response(HttpStatus.BAD_REQUEST, "Invalid request fields", request, errors);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StandardError> invalidArgument(IllegalArgumentException ex, HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, ex.getMessage(), request, List.of());
    }
    private ResponseEntity<StandardError> response(HttpStatus status, String message,
                                                   HttpServletRequest request, List<StandardError.FieldError> errors) {
        return ResponseEntity.status(status).body(new StandardError(Instant.now(), status.value(),
            status.getReasonPhrase(), message, request.getRequestURI(), errors));
    }
}
