package io.github.springdddtemplate.interfaces.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/// MVC configuration that enables Spring Framework 7's native API versioning support.
///
/// Spring Framework 7 introduces first-class API versioning via WebMvcConfigurer#configureApiVersioning.
/// This replaces the need for custom RequestMappingHandlerMapping subclasses or path-prefix tricks.
///
/// Two resolution strategies are configured:
///   1. Request header:    GET /api/users  +  API-Version: 2
///   2. Request parameter: GET /api/users?api-version=2
///
/// Controllers declare version constraints directly on @GetMapping / @PostMapping etc. via
/// the native `version` attribute introduced in Spring Framework 7, e.g.:
///   @GetMapping(value = "/{id}", version = "1")   → responds to version 1 only
///   @GetMapping(value = "/{id}", version = "2+")  → responds to version 2 and above
///   @GetMapping(value = "/{id}")                  → unversioned fallback (lowest priority)
///
/// The framework automatically detects supported versions from the declared version attributes,
/// and rejects requests with unknown versions with a 400 InvalidApiVersionException.
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void configureApiVersioning(ApiVersionConfigurer configurer) {
        configurer
                .useRequestHeader("API-Version")
                .useRequestParam("api-version")
                .setDefaultVersion("1");
    }
}
