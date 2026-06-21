package io.github.springdddtemplate.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/// User update request DTO - Java record for immutable update payload.
/// Only mutable fields are included; username is typically immutable in domain.
@Schema(description = "Request payload for updating an existing user")
public record UpdateUserRequest(
        @NotBlank @Size(min = 8, max = 100)
        @Schema(description = "New password", example = "newSecurePass456")
        String password,

        @NotBlank @Email
        @Schema(description = "New email address", example = "john.new@example.com")
        String email,

        @NotBlank
        @Schema(description = "New role: ADMIN, MEMBER, or GUEST", example = "ADMIN")
        String role
) {
}

