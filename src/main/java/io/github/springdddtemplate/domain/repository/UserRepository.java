package io.github.springdddtemplate.domain.repository;

import io.github.springdddtemplate.domain.model.entity.User;

import java.util.Optional;

/// Infrastructure layer provides concrete implementations.
/// This follows the DDD principle of dependency inversion: domain defines what it needs,
/// infrastructure fulfills it.
public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    void deleteById(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
