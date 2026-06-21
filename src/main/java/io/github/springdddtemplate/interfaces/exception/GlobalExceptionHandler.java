package io.github.springdddtemplate.interfaces.exception;

import io.github.springdddtemplate.domain.exception.BusinessException;
import io.github.springdddtemplate.domain.exception.ErrorCode;
import io.github.springdddtemplate.interfaces.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/// Global REST exception handler - part of the DDD Interface layer.
/// Provides uniform error response format across all controllers.
/// Key design: uses pattern matching for switch over the sealed ErrorCode hierarchy
/// (Java 21+ feature) to determine HTTP status codes. This eliminates the need
/// for if-else chains and ensures exhaustive handling of all error categories.
/// The compiler guarantees all sealed ErrorCode variants are handled,
/// so adding a new ErrorCode type will cause a compile error here until it's addressed.
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /// Handle BusinessException using pattern matching over sealed ErrorCode.
    /// Pattern matching for switch guarantees exhaustive handling of all ErrorCode variants:
    /// BusinessError, NotFoundError, ValidationError, AuthError.
    /// No default branch needed - the compiler verifies completeness.
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
        log.warn("Business exception: [{}] {}", ex.getErrorCode().code(), ex.getMessage());

        // Pattern matching for switch over sealed ErrorCode - exhaustive, no default needed
        var status = switch (ex.getErrorCode()) {
            case ErrorCode.BusinessError _ -> HttpStatus.CONFLICT;             // 409
            case ErrorCode.NotFoundError _ -> HttpStatus.NOT_FOUND;            // 404
            case ErrorCode.ValidationError _ -> HttpStatus.BAD_REQUEST;        // 400
            case ErrorCode.AuthError _ -> HttpStatus.UNAUTHORIZED;             // 401
        };

        var response = ApiResponse.<Void>error(ex.getErrorCode().code(), ex.getMessage());
        return ResponseEntity.status(status).body(response);
    }

    /// Handle Spring Validation errors (@Valid failures on @RequestBody).
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        // Using var + stream pipeline for concise field error aggregation
        var fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Validation error: {}", fieldErrors);
        var response = ApiResponse.<Void>error("VALIDATION_FAILED", fieldErrors);
        return ResponseEntity.badRequest().body(response);
    }

    /// Handle constraint violations (@Validated failures on @PathVariable, @RequestParam).
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(ConstraintViolationException ex) {
        var violations = ex.getConstraintViolations().stream()
                .map(violation -> {
                    // Extract field name from property path (e.g. "getUser.id" -> "id")
                    var path = violation.getPropertyPath().toString();
                    var fieldName = path.contains(".")
                            ? path.substring(path.lastIndexOf('.') + 1)
                            : path;
                    return fieldName + ": " + violation.getMessage();
                })
                .collect(Collectors.joining(", "));

        log.warn("Constraint violation: {}", violations);
        var response = ApiResponse.<Void>error("CONSTRAINT_VIOLATION", violations);
        return ResponseEntity.badRequest().body(response);
    }

    /// Handle authentication failures.
    /// since we only need the type match, not the exception details.
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        var response = ApiResponse.<Void>error("AUTH_FAILED", "Invalid credentials");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /// Handle access denied (authorization) failures.
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        var response = ApiResponse.<Void>error("ACCESS_DENIED", "You don't have permission to access this resource");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /// Catch-all handler for unexpected exceptions.
    /// Prevents stack traces from leaking to clients.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        var response = ApiResponse.<Void>error("INTERNAL_ERROR", "An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
