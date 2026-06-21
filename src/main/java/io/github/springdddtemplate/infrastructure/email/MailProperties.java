package io.github.springdddtemplate.infrastructure.email;

import org.springframework.boot.context.properties.ConfigurationProperties;

/// Mail configuration properties - modeled as a Java record for immutable configuration.
/// Spring Boot 3.x+ supports constructor binding with records,
/// making configuration classes both immutable and concise.
/// No setter methods needed - properties are bound through the canonical constructor.
/// Java record pattern for @ConfigurationProperties:
/// - Constructor binding is automatic for records (no @ConstructorBinding needed since Spring Boot 3.x)
/// - Naturally immutable and thread-safe
/// - Requires @EnableConfigurationProperties or @ConfigurationPropertiesScan
@ConfigurationProperties(prefix = "app.mail")
public record MailProperties(String from) {
    // Default value fallback via compact constructor
    public MailProperties {
        if (from == null) {
            from = "noreply@example.com";
        }
    }
}
