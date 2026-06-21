package io.github.springdddtemplate.application.dto;

import io.github.springdddtemplate.interfaces.validation.ValidRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/// User creation request DTO - Java record provides immutable, compact data carrier.
/// Records are ideal for DTOs: no boilerplate, automatic equals/hashCode/toString,
/// and compact constructors for validation.
/// Bean Validation annotations provide input-level format/structure checks,
/// while domain-level validation (UserDomainService) handles business rules.
@Schema(description = "Request payload for creating a new user")
public record CreateUserRequest(
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username must contain only letters, digits, and underscores")
        @Schema(description = "Username", example = "john_doe")
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        @Schema(description = "Password (min 8 chars)", example = "securePass123")
        String password,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be a valid address")
        @Schema(description = "Email address", example = "john@example.com")
        String email,

        @NotBlank(message = "Role is required")
        @ValidRole
        @Schema(description = "User role: ADMIN, MEMBER, or GUEST", example = "MEMBER")
        String role
) {
}

