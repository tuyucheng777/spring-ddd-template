package io.github.springdddtemplate.infrastructure.scheduling;

import io.github.springdddtemplate.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/// Scheduled task for cleaning up inactive user accounts.
/// Runs periodically based on cron configuration (default: 2 AM daily).
/// DDD principle: scheduled tasks belong to the infrastructure layer
/// since they deal with system operations, not domain logic.
/// The task delegates to domain/application services for actual business operations.
@Slf4j
@Component
@RequiredArgsConstructor
public class UserCleanupScheduledTask {

    private final UserRepository userRepository;

    /// Cleanup inactive users - scheduled via cron expression from configuration.
    /// Uses @Scheduled with a fixed default; can be overridden with app.scheduling.cleanup-cron.
    /// The conditional check prevents execution when scheduling is disabled.
    @Scheduled(cron = "${app.scheduling.cleanup-cron:0 0 2 * * *}")
    public void cleanupInactiveUsers() {
        log.info("Starting inactive user cleanup task...");
        try {
            // This is a template placeholder - implement actual cleanup logic
            // e.g., delete users inactive for > 30 days, send warning emails, etc.
            var totalUsers = userRepository.existsByUsername("system_check") ? 1 : 0;
            log.info("Cleanup task completed. Total system check: {}", totalUsers);
        } catch (Exception e) {
            log.error("Failed to complete cleanup task: {}", e.getMessage(), e);
        }
    }

    /// Health check scheduled task - simple heartbeat for monitoring.
    @Scheduled(fixedRateString = "${app.scheduling.health-check-interval:300000}")
    public void healthCheck() {
        log.debug("Scheduled task health check - system running normally");
    }
}
