package io.github.springdddtemplate.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/// User response DTO - Java record for immutable response payload.
/// Records ensure DTOs are naturally immutable and thread-safe,
/// perfect for REST API responses that should not be modified after creation.
@Schema(description = "User information response")
public record UserResponse(
        @Schema(description = "User ID", example = "1")
        Long id,

        @Schema(description = "Username", example = "john_doe")
        String username,

        @Schema(description = "Email address", example = "john@example.com")
        String email,

        @Schema(description = "User role display name", example = "Member")
        String role,

        @Schema(description = "Whether the user account is active", example = "true")
        boolean enabled
) {
}

