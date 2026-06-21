package io.github.springdddtemplate.infrastructure.messaging;

import io.github.springdddtemplate.domain.event.DomainEvent;
import io.github.springdddtemplate.domain.publisher.DomainEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/// Fallback domain event publisher for when RabbitMQ is disabled.
/// Logs events instead of publishing them to a message broker.
/// This ensures the application can run without RabbitMQ infrastructure
/// (e.g., in development or testing environments).
/// Activated when app.messaging.enabled=false.
@Slf4j
@Service
@ConditionalOnProperty(name = "app.messaging.enabled", havingValue = "false")
public class LoggingEventPublisher implements DomainEventPublisher {

    @Override
    public void publish(DomainEvent event) {
        log.info("Domain event published (logging only): [{}] at {}", event, event.occurredAt());
    }

    @Override
    public void publishAll(DomainEvent... events) {
        for (var event : events) {
            publish(event);
        }
    }
}
