package io.github.springdddtemplate.domain.model.valueobject;

/// User role hierarchy modeled as a sealed interface (Java 17+ feature).
/// Sealed interfaces explicitly declare which classes are permitted to implement them,
/// enabling exhaustive pattern matching in switch expressions.
/// This is ideal for DDD where domain concepts have well-defined, closed sets of variants.
public sealed interface UserRole permits UserRole.Admin, UserRole.Member, UserRole.Guest {

    /// Pattern matching for switch (Java 21+ finalized) -
    /// allows exhaustive matching over sealed hierarchies without default branch.
    String displayName();

    record Admin() implements UserRole {
        @Override
        public String displayName() {
            return "Administrator";
        }
    }

    record Member() implements UserRole {
        @Override
        public String displayName() {
            return "Member";
        }
    }

    record Guest() implements UserRole {
        @Override
        public String displayName() {
            return "Guest";
        }
    }

    /// Factory method using pattern matching for switch to derive role from string.
    static UserRole from(String role) {
        return switch (role.toUpperCase()) {
            case "ADMIN" -> new Admin();
            case "MEMBER" -> new Member();
            case "GUEST" -> new Guest();
            // Unnamed variable pattern (_) for irrelevant binding - Java 22+ feature
            case String _ -> throw new IllegalArgumentException("Unknown role: " + role);
        };
    }
}
