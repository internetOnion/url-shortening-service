package com.reaksmey.url_shortening_service;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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

    // private ErrorResponse createErrorResponse(
    //     HttpStatus status,
    //     String message,
    //     Map<String, String> validationErrors
    // ) {
    //     return new ErrorResponse(
    //         LocalDateTime.now(),
    //         status.value(),
    //         status.getReasonPhrase(),
    //         message,
    //         validationErrors
    //     );
    // }

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
        ErrorResponse response = createErrorResponse(
            HttpStatus.BAD_REQUEST,
            ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
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
