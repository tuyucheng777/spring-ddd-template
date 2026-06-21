package io.github.springdddtemplate.interfaces.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/// Unified API response wrapper - modeled as a Java record.
/// Provides consistent response structure across all endpoints,
/// making it easy for clients to parse and handle responses uniformly.
/// Generic type T allows wrapping any response body type.
/// Records ensure immutability, thread-safety, and automatic accessor generation.
/// Pattern matching for sealed ErrorCode is used in error responses
/// to determine appropriate HTTP status codes (see GlobalExceptionHandler).
@Schema(description = "Unified API response wrapper")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        @Schema(description = "Whether the request was successful", example = "true")
        @JsonProperty("success")
        boolean successful,

        @Schema(description = "Response data payload")
        T data,

        @Schema(description = "Error code if request failed", example = "USER_NOT_FOUND")
        String errorCode,

        @Schema(description = "Error message if request failed", example = "User not found with id: 1")
        String errorMessage,

        @Schema(description = "Response timestamp", example = "2026-06-21T10:30:00Z")
        Instant timestamp
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, null, Instant.now());
    }

    /// Factory method for successful responses without data (e.g., DELETE).
    public static <T> ApiResponse<T> empty() {
        return new ApiResponse<>(true, null, null, null, Instant.now());
    }

    /// Factory method for error responses.
    public static <T> ApiResponse<T> error(String errorCode, String errorMessage) {
        return new ApiResponse<>(false, null, errorCode, errorMessage, Instant.now());
    }
}
