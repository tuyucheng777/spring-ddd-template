package io.github.springdddtemplate.domain.service;

import io.github.springdddtemplate.domain.exception.BusinessException;
import io.github.springdddtemplate.domain.model.entity.User;
import io.github.springdddtemplate.domain.model.valueobject.EmailAddress;
import io.github.springdddtemplate.domain.model.valueobject.UserRole;
import io.github.springdddtemplate.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/// Domain service - encapsulates cross-entity business rules and invariants.
/// Uses pattern matching for switch over sealed UserRole hierarchy (Java 21+),
/// ensuring exhaustive handling of all role variants.
@Service
@RequiredArgsConstructor
public class UserDomainService {

    private final UserRepository userRepository;

    /// Validate that a new user can be created without violating domain invariants.
    public void validateNewUser(String username, String email, String role) {
        // Using var (Java 10+) for obvious type inference
        var emailAddress = new EmailAddress(email);
        var userRole = UserRole.from(role);

        if (userRepository.existsByUsername(username)) {
            throw BusinessException.business("USER_DUPLICATE_USERNAME",
                    "Username already exists: " + username);
        }

        if (userRepository.existsByEmail(emailAddress.value())) {
            throw BusinessException.business("USER_DUPLICATE_EMAIL",
                    "Email already exists: " + email);
        }

        // Pattern matching for switch over sealed UserRole - exhaustive, no default needed
        switch (userRole) {
            case UserRole.Admin _ -> validateAdminCreation();
            case UserRole.Member _ -> {
            } // No additional validation for Member
            case UserRole.Guest _ -> {
            } // No additional validation for Guest
        }
    }

    /// Validate admin creation - only one admin can exist in the system.
    private void validateAdminCreation() {
        // Business rule: restrict admin creation
        // This is a placeholder - customize according to your domain rules
    }

    /// Determine if user can perform a given action based on role.
    /// Demonstrates pattern matching with sealed types for authorization logic.
    public boolean canPerformAction(User user, String action) {
        var role = UserRole.from(user.getRole());
        return switch (role) {
            case UserRole.Admin _ -> true; // Admin can do everything
            case UserRole.Member _ -> switch (action) {
                case "READ", "WRITE", "UPDATE" -> true;
                case String _ -> false;
            };
            case UserRole.Guest _ -> "READ".equals(action);
        };
    }
}
