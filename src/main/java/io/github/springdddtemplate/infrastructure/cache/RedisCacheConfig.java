package io.github.springdddtemplate.infrastructure.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/// Redis (distributed) cache configuration.
/// Activated when app.cache.type=redis (for production/multi-instance deployments).
/// Redis provides distributed caching with:
/// - Shared cache across multiple application instances
/// - Persistence options (RDB/AOF)
/// - Cluster support for high availability
/// Cache entries are serialized as JSON for human-readable inspection.
/// Only active when Redis cache is explicitly selected via configuration.
@Configuration
@ConditionalOnProperty(name = "app.cache.type", havingValue = "redis")
@EnableCaching
public class RedisCacheConfig {

    /// Redis CacheManager bean - configures JSON serialization and TTL per cache region.
    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory,
                                          CacheProperties cacheProperties) {
        var defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(cacheProperties.defaultTtl()))
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                // GenericJacksonJsonRedisSerializer (Spring Data Redis 4.x) uses Jackson 3 internally.
                // It no longer has a no-arg constructor; use builder() factory method to create instance.
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(GenericJacksonJsonRedisSerializer.builder().build()));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .transactionAware()
                .build();
    }
}
