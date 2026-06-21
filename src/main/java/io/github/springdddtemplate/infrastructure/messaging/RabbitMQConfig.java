package io.github.springdddtemplate.infrastructure.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/// RabbitMQ topology configuration - defines exchanges, queues, and bindings.
/// Uses TopicExchange for flexible routing: events are published with routing keys
/// matching the event type (e.g., "user.created", "user.activated").
/// DDD principle: each domain event type gets its own queue, enabling
/// independent consumers to process different event types at their own pace.
/// Exchange naming follows the pattern: {domain}.{context}.{purpose}
/// Queue naming follows: {prefix}.{event-type}
@Configuration
@ConditionalOnProperty(name = "app.messaging.enabled", havingValue = "true")
public class RabbitMQConfig {

    /// Topic exchange for all domain events.
    /// TopicExchange allows routing keys with wildcards (e.g., "user.*" matches all user events).
    @Bean
    public TopicExchange domainEventExchange(MessagingProperties properties) {
        return new TopicExchange(properties.exchange(), true, false);
    }

    /// Queue for UserCreated events.
    @Bean
    public Queue userCreatedQueue(MessagingProperties properties) {
        return QueueBuilder.durable(properties.queuePrefix() + "user.created")
                .build();
    }

    /// Queue for UserActivated events.
    @Bean
    public Queue userActivatedQueue(MessagingProperties properties) {
        return QueueBuilder.durable(properties.queuePrefix() + "user.activated")
                .build();
    }

    /// Queue for UserDeactivated events.
    @Bean
    public Queue userDeactivatedQueue(MessagingProperties properties) {
        return QueueBuilder.durable(properties.queuePrefix() + "user.deactivated")
                .build();
    }

    /// Binding: UserCreated queue -> domain event exchange with routing key "user.created".
    @Bean
    public Binding userCreatedBinding(TopicExchange exchange, Queue userCreatedQueue) {
        return BindingBuilder.bind(userCreatedQueue).to(exchange).with("user.created");
    }

    /// Binding: UserActivated queue -> domain event exchange with routing key "user.activated".
    @Bean
    public Binding userActivatedBinding(TopicExchange exchange, Queue userActivatedQueue) {
        return BindingBuilder.bind(userActivatedQueue).to(exchange).with("user.activated");
    }

    /// Binding: UserDeactivated queue -> domain event exchange with routing key "user.deactivated".
    @Bean
    public Binding userDeactivatedBinding(TopicExchange exchange, Queue userDeactivatedQueue) {
        return BindingBuilder.bind(userDeactivatedQueue).to(exchange).with("user.deactivated");
    }

    /// JSON message converter for serializing domain events as JSON.
    /// Ensures events are human-readable and interoperable across different consumers.
    /// In Spring AMQP 4.x, Jackson2JsonMessageConverter is deprecated;
    /// Spring Boot auto-configuration provides the appropriate converter.
    /// This bean uses the auto-configured ObjectMapper from Spring Boot.
    @SuppressWarnings("deprecation")
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new org.springframework.amqp.support.converter.Jackson2JsonMessageConverter();
    }
}
