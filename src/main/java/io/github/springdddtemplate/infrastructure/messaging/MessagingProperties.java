package io.github.springdddtemplate.infrastructure.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

/// RabbitMQ messaging configuration properties - record-based immutable configuration.
/// Controls RabbitMQ connection, exchange, and queue naming conventions.
/// DDD principle: messaging configuration belongs to infrastructure layer.
@ConfigurationProperties(prefix = "app.messaging")
public record MessagingProperties(
        /// RabbitMQ exchange name for domain events
        String exchange,
        /// Queue name prefix for domain event queues
        String queuePrefix,
        /// Whether to enable RabbitMQ event publishing
        boolean enabled
) {
    public MessagingProperties {
        if (exchange == null) {
            exchange = "ddd.domain.events";
        }
        if (queuePrefix == null) {
            queuePrefix = "ddd.";
        }
        // Default enabled=true when RabbitMQ dependencies are present
    }
}
