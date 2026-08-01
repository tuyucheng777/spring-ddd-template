package io.github.springdddtemplate.domain.model.entity;

import io.github.springdddtemplate.domain.model.valueobject.EmailAddress;
import io.github.springdddtemplate.domain.model.valueobject.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/// User entity - core domain model.
/// Uses Lombok for boilerplate reduction while keeping domain logic explicit.
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    /// Role stored as string in DB, mapped to sealed interface hierarchy in domain.
    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private boolean enabled;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    /// Domain behavior: activate a user account.
    public void activate() {
        this.enabled = true;
    }

    /// Domain behavior: deactivate a user account.
    public void deactivate() {
        this.enabled = false;
    }

    /// Domain behavior: change email using value object validation.
    /// Uses var (Java 10+ feature) for concise local variable declaration
    /// when the type is obvious from the right-hand side.
    public void changeEmail(String newEmail) {
        var emailAddress = new EmailAddress(newEmail);
        this.email = emailAddress.value();
    }

    /// Domain behavior: change role using sealed hierarchy.
    public void changeRole(String newRole) {
        var role = UserRole.from(newRole);
        this.role = role.displayName();
    }
}
