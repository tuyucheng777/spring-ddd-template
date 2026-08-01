package io.github.springdddtemplate.interfaces.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/// Swagger / OpenAPI configuration with multi-version grouping.
///
/// Spring Framework 7 API versioning is resolved at request time via the API-Version header
/// or api-version query parameter (see WebMvcConfig). Springdoc does not natively understand
/// Spring Framework 7 version attributes yet, so we expose two GroupedOpenApi beans — one
/// per API version — using OpenAPI operationId prefixes as the discriminator.
///
/// Each group targets the same /api/users/** path but is labelled v1 / v2 so that
/// Swagger UI provides separate tabs for each version. Consumers can browse the v2 group
/// and see the enriched UserResponseV2 schema alongside the standard UserResponse in v1.
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Spring DDD Template API")
                        .description("""
                                REST API for the Spring DDD Template project.
                                Built with Spring Boot 4, Spring Framework 7 (native API versioning), \
                                JPA, Security, MapStruct, and Mail.

                                **API Versioning** — pass the desired version via:
                                - Request header: `API-Version: 2`
                                - Query parameter: `?api-version=2`
                                - Omit for default (version 1)
                                """)
                        .version("2.0.0")
                        .contact(new Contact()
                                .name("Spring DDD Template")
                                .url("https://github.com/spring-ddd-template"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local development server")
                ));
    }

    /// Swagger group for API v1 — shows endpoints returning UserResponse.
    @Bean
    public GroupedOpenApi v1Api() {
        return GroupedOpenApi.builder()
                .group("v1")
                .displayName("API v1")
                .pathsToMatch("/api/users/**")
                .addOperationCustomizer((operation, handlerMethod) -> {
                    var summary = operation.getSummary();
                    // Only include operations that belong to v1 (contain "(v1)" or are version-neutral)
                    if (summary != null && summary.contains("(v2")) {
                        return null;
                    }
                    return operation;
                })
                .build();
    }

    /// Swagger group for API v2 — shows endpoints returning UserResponseV2.
    @Bean
    public GroupedOpenApi v2Api() {
        return GroupedOpenApi.builder()
                .group("v2")
                .displayName("API v2")
                .pathsToMatch("/api/users/**")
                .addOperationCustomizer((operation, handlerMethod) -> {
                    var summary = operation.getSummary();
                    // Only include operations that belong to v2 (contain "(v2") or are version-neutral
                    if (summary != null && summary.contains("(v1)")) {
                        return null;
                    }
                    return operation;
                })
                .build();
    }
}
