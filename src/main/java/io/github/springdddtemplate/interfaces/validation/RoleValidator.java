package io.github.springdddtemplate.interfaces.validation;

import io.github.springdddtemplate.domain.model.valueobject.UserRole;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/// Constraint validator for @ValidRole - delegates to domain's UserRole.from() factory.
/// If UserRole.from() throws IllegalArgumentException, the role string is invalid.
/// This design follows DDD principles: the validator uses domain logic (sealed UserRole)
/// rather than hardcoding valid values in the interface layer.
/// Changes to the UserRole hierarchy automatically propagate here without duplication.
public class RoleValidator implements ConstraintValidator<ValidRole, String> {

    @Override
    public boolean isValid(String role, ConstraintValidatorContext context) {
        if (role == null || role.isBlank()) {
            // Let @NotBlank handle null/blank - don't duplicate that check
            return true;
        }

        try {
            UserRole.from(role);
            return true;
        } catch (IllegalArgumentException _) {
            return false;
        }
    }
}
