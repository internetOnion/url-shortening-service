package com.reaksmey.url_shortening_service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private ErrorResponse createErrorResponse(
        HttpStatus status,
        String message
    ) {
        return new ErrorResponse(
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            message
        );
    }

    private ErrorResponse createErrorResponse(
        HttpStatus status,
        String message,
        Map<String, String> validationErrors
    ) {
        return new ErrorResponse(
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            message,
            validationErrors
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
        ResourceNotFoundException ex
    ) {
        log.debug("Resource not found");
        ErrorResponse response = createErrorResponse(
            HttpStatus.NOT_FOUND,
            ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException ex
    ) {
        log.debug("Method argument not valid");
        Map<String, String> errors = ex
            .getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(
                Collectors.toMap(
                    FieldError::getField,
                    FieldError::getDefaultMessage,
                    (existing, replacement) -> existing
                )
            );

        ErrorResponse response = createErrorResponse(
            HttpStatus.BAD_REQUEST,
            "Validation failed",
            errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
        HttpMessageNotReadableException ex
    ) {
        log.debug("Request body is missing or malformed");
        ErrorResponse response = createErrorResponse(
            HttpStatus.BAD_REQUEST,
            "Request body is required"
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(
        NoResourceFoundException ex
    ) {
        log.debug("No resource found");
        ErrorResponse response = createErrorResponse(
            HttpStatus.NOT_FOUND,
            "Resource not found"
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);
        ErrorResponse errorResponse = createErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            errorResponse
        );
    }
}
