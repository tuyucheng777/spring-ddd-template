package io.github.springdddtemplate.infrastructure.messaging;

import io.github.springdddtemplate.domain.event.DomainEvent;
import io.github.springdddtemplate.domain.publisher.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/// RabbitMQ-based domain event publisher - implements the domain's DomainEventPublisher interface.
/// DDD dependency inversion: domain defines the contract (DomainEventPublisher),
/// infrastructure provides the implementation (RabbitMQEventPublisher).
/// This keeps domain code pure and unaware of messaging technology.
/// Uses pattern matching for switch over sealed DomainEvent hierarchy
/// to determine routing keys for each event type.
/// Only active when messaging is enabled via app.messaging.enabled=true.
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.messaging.enabled", havingValue = "true", matchIfMissing = true)
public class RabbitMQEventPublisher implements DomainEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final MessagingProperties messagingProperties;

    @Override
    public void publish(DomainEvent event) {
        // Pattern matching for switch over sealed DomainEvent - exhaustive routing
        var routingKey = switch (event) {
            case DomainEvent.UserCreated _ -> "user.created";
            case DomainEvent.UserActivated _ -> "user.activated";
            case DomainEvent.UserDeactivated _ -> "user.deactivated";
        };

        log.info("Publishing domain event [{}] to exchange [{}] with routing key [{}]",
                event.getClass().getSimpleName(), messagingProperties.exchange(), routingKey);

        rabbitTemplate.convertAndSend(messagingProperties.exchange(), routingKey, event);
    }

    @Override
    public void publishAll(DomainEvent... events) {
        for (var event : events) {
            publish(event);
        }
    }
}
