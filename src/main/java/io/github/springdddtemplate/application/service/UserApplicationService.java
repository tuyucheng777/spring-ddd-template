package io.github.springdddtemplate.application.service;

import io.github.springdddtemplate.application.dto.CreateUserRequest;
import io.github.springdddtemplate.application.dto.UpdateUserRequest;
import io.github.springdddtemplate.application.dto.UserResponse;
import io.github.springdddtemplate.application.dto.UserResponseV2;
import io.github.springdddtemplate.application.mapper.UserMapper;
import io.github.springdddtemplate.domain.event.DomainEvent;
import io.github.springdddtemplate.domain.exception.BusinessException;
import io.github.springdddtemplate.domain.publisher.DomainEventPublisher;
import io.github.springdddtemplate.domain.repository.UserRepository;
import io.github.springdddtemplate.domain.service.UserDomainService;
import io.github.springdddtemplate.infrastructure.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/// Application service (use case orchestrator) -
/// coordinates domain objects, infrastructure services, and mappers
/// to fulfill user management use cases.
/// Does NOT contain business logic itself - delegates to domain service.
/// Responsible for: transaction management, DTO mapping, cross-layer coordination,
/// and domain event publishing after successful state transitions.
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserApplicationService {

    private final UserRepository userRepository;
    private final UserDomainService userDomainService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final DomainEventPublisher eventPublisher;

    /// Create a new user - orchestrates validation, persistence, notification, and event publishing.
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        // Delegate domain validation
        userDomainService.validateNewUser(request.username(), request.email(), request.role());

        // Map DTO to entity and encode password
        var user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));

        // Persist
        var saved = userRepository.save(user);

        // Publish domain event via DomainEventPublisher (RabbitMQ or logging fallback)
        eventPublisher.publish(new DomainEvent.UserCreated(
                saved.getId(), saved.getUsername(), saved.getEmail(), saved.getRole(), Instant.now()));

        // Send welcome email (async consideration possible)
        emailService.sendWelcomeEmail(saved.getEmail(), saved.getUsername());

        return userMapper.toResponse(saved);
    }

    /// Get user by ID.
    public UserResponse getUser(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("USER_NOT_FOUND",
                        "User not found with id: " + id));
        return userMapper.toResponse(user);
    }

    /// Update an existing user.
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("USER_NOT_FOUND",
                        "User not found with id: " + id));

        userMapper.updateEntityFromRequest(request, user);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.changeEmail(request.email());
        user.changeRole(request.role());

        var updated = userRepository.save(user);
        return userMapper.toResponse(updated);
    }

    /// Delete a user by ID.
    @Transactional
    public void deleteUser(Long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw BusinessException.notFound("USER_NOT_FOUND", "User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    /// Activate a user account - publishes UserActivated event after successful activation.
    @Transactional
    public UserResponse activateUser(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("USER_NOT_FOUND",
                        "User not found with id: " + id));
        user.activate();
        var saved = userRepository.save(user);

        eventPublisher.publish(new DomainEvent.UserActivated(saved.getId(), saved.getUsername(), Instant.now()));

        return userMapper.toResponse(saved);
    }

    /// Deactivate a user account - publishes UserDeactivated event after successful deactivation.
    @Transactional
    public UserResponse deactivateUser(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("USER_NOT_FOUND",
                        "User not found with id: " + id));
        user.deactivate();
        var saved = userRepository.save(user);

        eventPublisher.publish(new DomainEvent.UserDeactivated(saved.getId(), saved.getUsername(), Instant.now()));

        return userMapper.toResponse(saved);
    }

    // -----------------------------------------------------------------------
    // V2 use cases — same orchestration logic, richer response payload
    // -----------------------------------------------------------------------

    /// V2: Create user — identical orchestration to V1, returns enriched V2 DTO.
    @Transactional
    public UserResponseV2 createUserV2(CreateUserRequest request) {
        userDomainService.validateNewUser(request.username(), request.email(), request.role());

        var user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));

        var saved = userRepository.save(user);

        eventPublisher.publish(new DomainEvent.UserCreated(
                saved.getId(), saved.getUsername(), saved.getEmail(), saved.getRole(), Instant.now()));

        emailService.sendWelcomeEmail(saved.getEmail(), saved.getUsername());

        return userMapper.toResponseV2(saved);
    }

    /// V2: Get user by ID — returns enriched V2 DTO with displayName and createdAt.
    public UserResponseV2 getUserV2(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("USER_NOT_FOUND",
                        "User not found with id: " + id));
        return userMapper.toResponseV2(user);
    }

    /// V2: Update user — returns enriched V2 DTO.
    @Transactional
    public UserResponseV2 updateUserV2(Long id, UpdateUserRequest request) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("USER_NOT_FOUND",
                        "User not found with id: " + id));

        userMapper.updateEntityFromRequest(request, user);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.changeEmail(request.email());
        user.changeRole(request.role());

        var updated = userRepository.save(user);
        return userMapper.toResponseV2(updated);
    }
}
