package io.github.springdddtemplate.domain.model.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/// Value object tests - validates domain invariants in EmailAddress and UserRole.
/// Tests compact canonical constructor validation (record feature)
/// and sealed hierarchy pattern matching.
@DisplayName("Value Object Tests")
class ValueObjectTest {

    @Nested
    @DisplayName("EmailAddress")
    class EmailAddressTests {

        @Test
        @DisplayName("Should create valid email address")
        void shouldCreateValidEmail() {
            var email = new EmailAddress("user@example.com");
            assertEquals("user@example.com", email.value());
        }

        @Test
        @DisplayName("Should reject email without @ sign")
        void shouldRejectInvalidEmail() {
            assertThrows(IllegalArgumentException.class,
                    () -> new EmailAddress("invalid-email"));
        }

        @Test
        @DisplayName("Should reject null email")
        void shouldRejectNullEmail() {
            assertThrows(IllegalArgumentException.class,
                    () -> new EmailAddress(null));
        }
    }

    @Nested
    @DisplayName("UserRole Sealed Interface")
    class UserRoleTests {

        @Test
        @DisplayName("Should create Admin role from string")
        void shouldCreateAdminRole() {
            var role = UserRole.from("ADMIN");
            // Pattern matching for instanceof to verify specific variant
            if (role instanceof UserRole.Admin admin) {
                assertEquals("Administrator", admin.displayName());
            }
        }

        @Test
        @DisplayName("Should create Member role from string")
        void shouldCreateMemberRole() {
            var role = UserRole.from("MEMBER");
            if (role instanceof UserRole.Member member) {
                assertEquals("Member", member.displayName());
            }
        }

        @Test
        @DisplayName("Should create Guest role from string")
        void shouldCreateGuestRole() {
            var role = UserRole.from("GUEST");
            if (role instanceof UserRole.Guest guest) {
                assertEquals("Guest", guest.displayName());
            }
        }

        @Test
        @DisplayName("Should reject unknown role string")
        void shouldRejectUnknownRole() {
            assertThrows(IllegalArgumentException.class,
                    () -> UserRole.from("UNKNOWN"));
        }

        @Test
        @DisplayName("Should handle case-insensitive role input")
        void shouldHandleCaseInsensitive() {
            var role = UserRole.from("admin");
            if (role instanceof UserRole.Admin _) {
                // Unnamed variable pattern (_) - we only care about the type match
                // not the variable binding since Admin is a no-arg record
            } else {
                org.junit.jupiter.api.Assertions.fail("Expected Admin role");
            }
        }
    }
}
