package io.github.springdddtemplate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;

/// Spring DDD Template Application entry point.
///
/// @ConfigurationPropertiesScan enables record-based configuration properties binding,
/// so @ConfigurationProperties records (like MailProperties) are automatically discovered
/// and bound without needing explicit @EnableConfigurationProperties declarations.
/// @EnableAsync enables @Async method execution for non-blocking operations (e.g., email sending).
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableAsync
public class SpringDddTemplateApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringDddTemplateApplication.class, args);
    }
}
