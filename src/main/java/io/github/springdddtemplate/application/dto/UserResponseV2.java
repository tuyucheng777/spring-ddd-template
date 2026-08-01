package io.github.springdddtemplate.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/// V2 user response DTO — extends V1 with additional fields introduced in API version 2.
///
/// Changes from V1 (UserResponse):
///   - displayName: human-readable full name or alias, separate from the login username
///   - createdAt:   account creation timestamp for audit and display purposes
///
/// Records remain the right choice: immutable, compact, naturally serialisable by Jackson.
@Schema(description = "User information response (v2)")
public record UserResponseV2(
        @Schema(description = "User ID", example = "1")
        Long id,

        @Schema(description = "Username", example = "john_doe")
        String username,

        @Schema(description = "Email address", example = "john@example.com")
        String email,

        @Schema(description = "User role display name", example = "Member")
        String role,

        @Schema(description = "Whether the user account is active", example = "true")
        boolean enabled,

        @Schema(description = "Human-readable display name", example = "John Doe")
        String displayName,

        @Schema(description = "Account creation timestamp", example = "2026-01-15T10:30:00Z")
        Instant createdAt
) {
}
