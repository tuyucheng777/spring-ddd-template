package io.github.springdddtemplate.infrastructure.persistence;

import io.github.springdddtemplate.domain.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/// JPA-based implementation of the domain UserRepository.
/// By extending JpaRepository, we get all standard CRUD operations for free.
/// The domain repository interface is bridged through this Spring Data JPA repository.
///
/// DDD principle: the domain layer defines the contract (UserRepository interface),
/// the infrastructure layer provides the implementation (JpaUserRepository).
public interface JpaUserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
