package io.github.springdddtemplate.infrastructure.persistence;

import io.github.springdddtemplate.domain.model.entity.User;
import io.github.springdddtemplate.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/// Adapter that bridges the domain UserRepository interface
/// to the Spring Data JPA JpaUserRepository implementation.
/// This is the DDD "anti-corruption layer" pattern:
/// domain code only knows about UserRepository (domain interface),
/// while this adapter delegates to the infrastructure-specific JPA repository.
///
/// Cache annotations are applied at the infrastructure adapter level,
/// NOT on the domain interface. This follows DDD's principle that
/// caching is an infrastructure concern - the domain should remain pure
/// and unaware of caching mechanics.
///
/// Cache strategy:
/// - @Cacheable on read operations: cache results to avoid DB hits
/// - @CachePut on save: update cache after writes to keep consistency
/// - @CacheEvict on delete: remove stale entries from cache
@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    @Override
    @Caching(put = {
            @CachePut(value = "userById", key = "#result.id"),
            @CachePut(value = "userByUsername", key = "#result.username")
    })
    public User save(User user) {
        return jpaUserRepository.save(user);
    }

    @Override
    @Cacheable(value = "userById", key = "#id")
    public Optional<User> findById(Long id) {
        return jpaUserRepository.findById(id);
    }

    @Override
    @Cacheable(value = "userByUsername", key = "#username")
    public Optional<User> findByUsername(String username) {
        return jpaUserRepository.findByUsername(username);
    }

    @Override
    @Cacheable(value = "userByEmail", key = "#email")
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email);
    }

    @Override
    @CacheEvict(value = "userById", key = "#id")
    public void deleteById(Long id) {
        jpaUserRepository.deleteById(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaUserRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }
}
