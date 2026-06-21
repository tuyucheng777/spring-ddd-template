package io.github.springdddtemplate.infrastructure.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;

/// Cache configuration properties - record-based immutable configuration.
/// Controls cache behavior for both local (Caffeine) and distributed (Redis) cache.
/// The cache.type property selects the cache strategy:
/// - "caffeine" for local in-memory cache (single-instance deployments)
/// - "redis" for distributed cache (multi-instance deployments)
@ConfigurationProperties(prefix = "app.cache")
public record CacheProperties(
        /// Cache provider type: "caffeine" (local) or "redis" (distributed)
        String type,
        /// Default time-to-live for cache entries (in seconds)
        long defaultTtl,
        /// Maximum size for local cache entries
        long maxSize
) {
    public CacheProperties {
        if (type == null) {
            type = "caffeine"; // Default to local cache for development
        }
        if (defaultTtl == 0) {
            defaultTtl = 300; // 5 minutes default TTL
        }
        if (maxSize == 0) {
            maxSize = 1000; // Max 1000 entries per cache
        }
    }
}
