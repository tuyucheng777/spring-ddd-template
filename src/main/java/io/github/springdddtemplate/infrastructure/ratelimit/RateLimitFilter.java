package io.github.springdddtemplate.infrastructure.ratelimit;

import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/// Rate limiting filter using Bucket4j token-bucket algorithm.
/// Bucket4j provides a mature, well-tested rate limiting implementation:
/// - Token bucket algorithm: allows burst requests up to capacity
/// - Refill mechanism: tokens are added periodically based on configuration
/// - Per-endpoint configuration: different limits for different API paths
/// - In-memory buckets: suitable for single-instance deployments.
/// For distributed rate limiting across multiple instances,
/// replace ConcurrentHashMap with Redis-backed Bucket4j (bucket4j-redis).
/// DDD principle: rate limiting is a cross-cutting infrastructure concern
/// applied at the web layer, not in domain/application code.
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.rate-limit.enabled", havingValue = "true")
@EnableConfigurationProperties(RateLimitProperties.class)
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitProperties rateLimitProperties;
    /// Per-IP bucket storage - each client IP gets its own bucket.
    /// For distributed rate limiting, replace this with Redis-backed proxy.
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        var clientKey = resolveClientKey(request);
        var bucket = buckets.computeIfAbsent(clientKey, _ -> createBucket(request));

        if (bucket.tryConsume(1)) {
            // Token consumed - request allowed
            filterChain.doFilter(request, response);
        } else {
            // No tokens available - request rejected
            log.warn("Rate limit exceeded for client [{}] on path [{}]", clientKey, request.getRequestURI());
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("""
                    {
                      "success": false,
                      "errorCode": "RATE_LIMIT_EXCEEDED",
                      "errorMessage": "Too many requests. Please try again later.",
                      "timestamp": "%s"
                    }
                    """.formatted(java.time.Instant.now()));
        }
    }

    /// Create a Bucket4j bucket for a specific client and request path.
    /// Uses per-endpoint configuration when available, otherwise falls back to defaults.
    /// Bucket4j 8.x builder API: Bucket.builder().addLimit(limit -> ...).build()
    private Bucket createBucket(HttpServletRequest request) {
        var pathConfig = rateLimitProperties.endpoints().get(request.getRequestURI());

        if (pathConfig != null) {
            // Parse per-endpoint config: "capacity,refillTokens,refillDuration"
            var parts = pathConfig.split(",");
            var capacity = Long.parseLong(parts[0]);
            var refillTokens = Long.parseLong(parts[1]);
            var refillDuration = Long.parseLong(parts[2]);
            return Bucket.builder()
                    .addLimit(limit -> limit.capacity(capacity)
                            .refillIntervally(refillTokens, Duration.ofSeconds(refillDuration)))
                    .build();
        }

        // Default bucket configuration
        return Bucket.builder()
                .addLimit(limit -> limit.capacity(rateLimitProperties.defaultCapacity())
                        .refillIntervally(rateLimitProperties.defaultRefillTokens(),
                                Duration.ofSeconds(rateLimitProperties.defaultRefillDuration())))
                .build();
    }

    /// Resolve client identification key for rate limiting.
    /// Uses X-Forwarded-For header (for proxied requests) or remote address.
    private String resolveClientKey(HttpServletRequest request) {
        var forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
