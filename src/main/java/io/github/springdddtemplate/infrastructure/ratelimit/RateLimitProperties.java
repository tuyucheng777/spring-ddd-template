package io.github.springdddtemplate.infrastructure.ratelimit;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/// Rate limiting configuration properties - record-based immutable configuration.
/// Defines rate limits per API endpoint path pattern.
/// Each limit specifies:
/// - capacity: maximum number of requests in a burst
/// - refillTokens: number of tokens added per refill period
/// - refillDuration: time interval for token refill (in seconds)
@ConfigurationProperties(prefix = "app.rate-limit")
public record RateLimitProperties(
        /// Whether rate limiting is enabled
        boolean enabled,
        /// Default capacity for unspecified endpoints
        long defaultCapacity,
        /// Default refill tokens per period
        long defaultRefillTokens,
        /// Default refill duration in seconds
        long defaultRefillDuration,
        /// Per-path rate limit overrides: key = path pattern, value = "capacity,refillTokens,refillDuration"
        Map<String, String> endpoints
) {
    public RateLimitProperties {
        if (defaultCapacity == 0) {
            defaultCapacity = 100; // Default: 100 requests burst
        }
        if (defaultRefillTokens == 0) {
            defaultRefillTokens = 10; // Default: 10 tokens per refill
        }
        if (defaultRefillDuration == 0) {
            defaultRefillDuration = 1; // Default: 1 second refill period
        }
        if (endpoints == null) {
            endpoints = Map.of();
        }
    }
}
