package io.github.springdddtemplate.domain.exception;

/// Sealed error code hierarchy (Java 17+ sealed interfaces).
/// Provides exhaustive, type-safe error categorization that the compiler can verify.
/// Each permit defines a specific error category with structured metadata.
/// Pattern matching for switch over sealed types guarantees no case is missed,
/// eliminating the need for default/fallback branches.
public sealed interface ErrorCode
        permits ErrorCode.BusinessError, ErrorCode.NotFoundError, ErrorCode.ValidationError, ErrorCode.AuthError {

    String code();

    String message();

    /// Business logic violations (e.g., duplicate email, insufficient permissions).
    record BusinessError(String code, String message) implements ErrorCode {
    }

    /// Resource not found errors.
    record NotFoundError(String code, String message) implements ErrorCode {
    }

    /// Input validation failures.
    record ValidationError(String code, String message) implements ErrorCode {
    }

    /// Authentication/authorization failures.
    record AuthError(String code, String message) implements ErrorCode {
    }
}
