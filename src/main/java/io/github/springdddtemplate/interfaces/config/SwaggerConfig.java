package io.github.springdddtemplate.interfaces.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/// Swagger / OpenAPI configuration.
/// Provides API documentation metadata and server configuration.
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Spring DDD Template API")
                        .description("REST API documentation for the Spring DDD Template project. "
                                + "Built with Spring Boot 4, JPA, Security, MapStruct, and Mail.")
                        .version("1.0.0")
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
}
