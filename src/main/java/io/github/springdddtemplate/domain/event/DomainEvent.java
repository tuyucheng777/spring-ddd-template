package io.github.springdddtemplate.domain.event;

import java.time.Instant;

/// Base domain event - modeled as a sealed interface (Java 17+).
/// Sealed interfaces explicitly declare permitted event types,
/// enabling exhaustive pattern matching in consumers and handlers.
/// All domain events carry a timestamp for temporal ordering.
/// This follows the DDD pattern where domain events capture
/// significant state changes that other parts of the system need to react to.
public sealed interface DomainEvent
        permits DomainEvent.UserCreated, DomainEvent.UserActivated, DomainEvent.UserDeactivated {

    Instant occurredAt();

    /// User created event - published when a new user registers.
    record UserCreated(Long userId, String username, String email, String role, Instant occurredAt)
            implements DomainEvent {
    }

    /// User activated event - published when a user account is activated.
    record UserActivated(Long userId, String username, Instant occurredAt)
            implements DomainEvent {
    }

    /// User deactivated event - published when a user account is deactivated.
    record UserDeactivated(Long userId, String username, Instant occurredAt)
            implements DomainEvent {
    }
}
