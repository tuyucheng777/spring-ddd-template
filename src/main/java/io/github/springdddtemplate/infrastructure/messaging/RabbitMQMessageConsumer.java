package io.github.springdddtemplate.infrastructure.messaging;

import io.github.springdddtemplate.domain.event.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/// RabbitMQ message consumer - processes domain events from queues.
/// DDD principle: consumers in the infrastructure layer react to domain events
/// by triggering side effects (e.g., sending emails, updating read models).
/// Uses pattern matching for switch over sealed DomainEvent to handle
/// each event type with exhaustive coverage - compiler guarantees all cases are handled.
/// Only active when messaging is enabled via app.messaging.enabled=true.
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.messaging.enabled", havingValue = "true", matchIfMissing = true)
public class RabbitMQMessageConsumer {

    /// Consume UserCreated events - trigger welcome email and audit logging.
    @RabbitListener(queues = "${app.messaging.queue-prefix}ddd.user.created")
    public void handleUserCreated(DomainEvent.UserCreated event) {
        log.info("Consumed UserCreated event: userId={}, username={}, email={}",
                event.userId(), event.username(), event.email());
        // Side effects can be triggered here:
        // - Send welcome email via EmailService
        // - Create audit log entry
        // - Update read model / projection
    }

    /// Consume UserActivated events - trigger activation notification.
    @RabbitListener(queues = "${app.messaging.queue-prefix}ddd.user.activated")
    public void handleUserActivated(DomainEvent.UserActivated event) {
        log.info("Consumed UserActivated event: userId={}, username={}",
                event.userId(), event.username());
    }

    /// Consume UserDeactivated events - trigger deactivation notification.
    @RabbitListener(queues = "${app.messaging.queue-prefix}ddd.user.deactivated")
    public void handleUserDeactivated(DomainEvent.UserDeactivated event) {
        log.info("Consumed UserDeactivated event: userId={}, username={}",
                event.userId(), event.username());
    }
}
