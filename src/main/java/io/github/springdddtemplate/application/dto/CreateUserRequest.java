package io.github.springdddtemplate.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/// User creation request DTO - Java record provides immutable, compact data carrier.
/// Records are ideal for DTOs: no boilerplate, automatic equals/hashCode/toString,
/// and compact constructors for validation.
@Schema(description = "Request payload for creating a new user")
public record CreateUserRequest(
        @NotBlank @Size(min = 3, max = 50)
        @Schema(description = "Username", example = "john_doe")
        String username,

        @NotBlank @Size(min = 8, max = 100)
        @Schema(description = "Password (min 8 chars)", example = "securePass123")
        String password,

        @NotBlank @Email
        @Schema(description = "Email address", example = "john@example.com")
        String email,

        @NotBlank
        @Schema(description = "User role: ADMIN, MEMBER, or GUEST", example = "MEMBER")
        String role
) {
}

