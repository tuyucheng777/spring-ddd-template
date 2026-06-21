package io.github.springdddtemplate.domain.publisher;

import io.github.springdddtemplate.domain.event.DomainEvent;

/// Domain event publisher interface - defined in the domain layer.
/// Following the DDD dependency inversion principle: the domain defines
/// the contract for publishing events, and the infrastructure layer
/// provides the concrete implementation (e.g., RabbitMQ, Kafka, in-memory).
/// This keeps the domain layer pure and decoupled from messaging infrastructure.
/// The application service orchestrates event publishing after successful domain operations.
public interface DomainEventPublisher {

    /// Publish a single domain event.
    void publish(DomainEvent event);

    /// Publish multiple domain events in batch.
    void publishAll(DomainEvent... events);
}
