package io.github.springdddtemplate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/// Spring DDD Template Application entry point.
///
/// @ConfigurationPropertiesScan enables record-based configuration properties binding,
/// so @ConfigurationProperties records (like MailProperties) are automatically discovered
/// and bound without needing explicit @EnableConfigurationProperties declarations.
/// @EnableAsync enables @Async method execution for non-blocking operations (e.g., email sending).
/// @EnableScheduling enables @Scheduled task execution (e.g., user cleanup, cache refresh).
/// @EnableCaching is placed on cache configuration classes (CaffeineCacheConfig / RedisCacheConfig)
/// instead of the main class, so test slices that don't load cache config won't fail
/// due to missing CacheManager bean.
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableAsync
@EnableScheduling
public class SpringDddTemplateApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringDddTemplateApplication.class, args);
    }
}
