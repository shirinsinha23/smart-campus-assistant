package com.example.smartcampusassistant.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private LocalDateTime timestamp;

    private int status;

    private String error;

    private String message;

    private String path;

    private Map<String, String> validationErrors;

    // Constructor for simple error responses
    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
    }

    // Constructor for validation errors
    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message, Map<String, String> validationErrors) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.validationErrors = validationErrors;
    }

    // Static factory methods for common use cases

    public static ErrorResponse of(HttpStatus status, String message) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .build();
    }

    public static ErrorResponse of(HttpStatus status, String message, Map<String, String> validationErrors) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .validationErrors(validationErrors)
                .build();
    }

    public static ErrorResponse notFound(String message) {
        return of(HttpStatus.NOT_FOUND, message);
    }

    public static ErrorResponse badRequest(String message) {
        return of(HttpStatus.BAD_REQUEST, message);
    }

    public static ErrorResponse internalError(String message) {
        return of(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}