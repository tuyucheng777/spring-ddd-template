package io.github.springdddtemplate.infrastructure.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/// Caffeine (local) cache configuration.
/// Activated when app.cache.type=caffeine (default for development).
/// Caffeine provides high-performance in-memory caching with:
/// - Time-based expiration (TTL)
/// - Size-based eviction
/// - Async refresh capabilities
/// Only active when local cache is selected via configuration.
@Configuration
@ConditionalOnProperty(name = "app.cache.type", havingValue = "caffeine", matchIfMissing = true)
@EnableCaching
public class CaffeineCacheConfig {

    /// Caffeine CacheManager bean - configures all cache regions with uniform TTL and size limits.
    /// Cache names are defined here and referenced by @Cacheable annotations.
    @Bean
    public CacheManager caffeineCacheManager(CacheProperties cacheProperties) {
        var cacheManager = new CaffeineCacheManager("users", "userById", "userByUsername", "userByEmail");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(cacheProperties.defaultTtl(), TimeUnit.SECONDS)
                .maximumSize(cacheProperties.maxSize()));
        return cacheManager;
    }
}
