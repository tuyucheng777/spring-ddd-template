package io.github.springdddtemplate.infrastructure.persistence;

import io.github.springdddtemplate.domain.model.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// JPA repository test - tests database operations using Spring Data JPA test infrastructure.
///
/// @DataJpaTest loads only JPA-related components:
/// - Entity classes
/// - JPA repositories
/// - TestEntityManager for direct entity manipulation
/// - H2 in-memory database (auto-configured)
/// No web layer, security, or application services are loaded.
/// This provides fast, focused testing of persistence logic.
@DataJpaTest
@DisplayName("JPA Repository Tests")
class JpaUserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Test
    @DisplayName("Should persist and retrieve user by ID")
    void shouldPersistAndRetrieveUser() {
        // Create and persist user using TestEntityManager
        var user = User.builder()
                .username("test_user")
                .password("encoded_password")
                .email("test@example.com")
                .role("MEMBER")
                .enabled(true)
                .build();

        var persisted = entityManager.persistAndFlush(user);

        // Retrieve via JPA repository
        var found = jpaUserRepository.findById(persisted.getId());

        assertTrue(found.isPresent());
        assertEquals("test_user", found.get().getUsername());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    @DisplayName("Should find user by username")
    void shouldFindByUsername() {
        var user = User.builder()
                .username("findable_user")
                .password("password")
                .email("findable@example.com")
                .role("MEMBER")
                .enabled(true)
                .build();
        entityManager.persistAndFlush(user);

        var found = jpaUserRepository.findByUsername("findable_user");

        assertTrue(found.isPresent());
        assertEquals("findable@example.com", found.get().getEmail());
    }

    @Test
    @DisplayName("Should check username existence correctly")
    void shouldCheckUsernameExistence() {
        var user = User.builder()
                .username("existing_user")
                .password("password")
                .email("existing@example.com")
                .role("GUEST")
                .enabled(true)
                .build();
        entityManager.persistAndFlush(user);

        assertTrue(jpaUserRepository.existsByUsername("existing_user"));
        assertFalse(jpaUserRepository.existsByUsername("non_existing_user"));
    }

    @Test
    @DisplayName("Should check email existence correctly")
    void shouldCheckEmailExistence() {
        var user = User.builder()
                .username("email_user")
                .password("password")
                .email("unique@example.com")
                .role("MEMBER")
                .enabled(true)
                .build();
        entityManager.persistAndFlush(user);

        assertTrue(jpaUserRepository.existsByEmail("unique@example.com"));
        assertFalse(jpaUserRepository.existsByEmail("other@example.com"));
    }

    @Test
    @DisplayName("Should delete user by ID")
    void shouldDeleteUser() {
        var user = User.builder()
                .username("deletable_user")
                .password("password")
                .email("deletable@example.com")
                .role("GUEST")
                .enabled(true)
                .build();
        var persisted = entityManager.persistAndFlush(user);

        jpaUserRepository.deleteById(persisted.getId());

        assertFalse(jpaUserRepository.findById(persisted.getId()).isPresent());
    }
}
