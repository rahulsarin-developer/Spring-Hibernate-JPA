package com.project.spring_hibernate_jpa.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.time.Instant;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public record ApiError(
            Instant timestamp,
            int status,
            String message,
            Map<String, String> details
    ) {}

    private ResponseEntity<ApiError> error(
            HttpStatus status,
            String message,
            Map<String, String> details) {

        return ResponseEntity.status(status)
                .body(new ApiError(
                        Instant.now(),
                        status.value(),
                        message,
                        details
                ));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ApiError> missing(ResourceNotFoundException e) {

        return error(
                HttpStatus.NOT_FOUND,
                e.getMessage(),
                Map.of()
        );
    }

    @ExceptionHandler({
            ConflictException.class,
            DataIntegrityViolationException.class,
            ObjectOptimisticLockingFailureException.class
    })
    ResponseEntity<ApiError> conflict(
            ConflictException ex,
            HttpServletRequest request
    ) {

        return error(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                Map.of(
                        "field", "email",
                        "reason", "DUPLICATE_EMAIL",
                        "path", request.getRequestURI()
                )
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ApiError> bad(IllegalArgumentException e) {

        return error(
                HttpStatus.BAD_REQUEST,
                e.getMessage(),
                Map.of()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> validation(MethodArgumentNotValidException e) {

        Map<String, String> fields = new LinkedHashMap<>();

        e.getBindingResult()
                .getFieldErrors()
                .forEach(f -> fields.put(
                        f.getField(),
                        f.getDefaultMessage()
                ));

        return error(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                fields
        );
    }
}
