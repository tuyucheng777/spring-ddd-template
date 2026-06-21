package io.github.springdddtemplate.infrastructure.scheduling;

import org.springframework.boot.context.properties.ConfigurationProperties;

/// Scheduling configuration properties - record-based immutable configuration.
/// Controls scheduled task behavior through application.yaml configuration.
/// Schedule expressions follow standard cron format:
/// second minute hour day-of-month month day-of-week
@ConfigurationProperties(prefix = "app.scheduling")
public record SchedulingProperties(
        /// Cron expression for inactive user cleanup task
        String cleanupCron,
        /// Whether scheduled tasks are enabled
        boolean enabled
) {
    public SchedulingProperties {
        if (cleanupCron == null) {
            cleanupCron = "0 0 2 * * *"; // Default: run at 2 AM every day
        }
    }
}
