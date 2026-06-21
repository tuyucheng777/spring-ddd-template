package io.github.springdddtemplate.application.dto;

import io.github.springdddtemplate.interfaces.validation.ValidRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/// User update request DTO - Java record for immutable update payload.
/// Only mutable fields are included; username is typically immutable in domain.
/// Bean Validation ensures input format before domain processing.
@Schema(description = "Request payload for updating an existing user")
public record UpdateUserRequest(
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        @Schema(description = "New password", example = "newSecurePass456")
        String password,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be a valid address")
        @Schema(description = "New email address", example = "john.new@example.com")
        String email,

        @NotBlank(message = "Role is required")
        @ValidRole
        @Schema(description = "New role: ADMIN, MEMBER, or GUEST", example = "ADMIN")
        String role
) {
}

