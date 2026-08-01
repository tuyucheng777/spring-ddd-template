package io.github.springdddtemplate.interfaces.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.springdddtemplate.application.dto.CreateUserRequest;
import io.github.springdddtemplate.application.dto.UserResponse;
import io.github.springdddtemplate.application.dto.UserResponseV2;
import io.github.springdddtemplate.application.service.UserApplicationService;
import io.github.springdddtemplate.domain.exception.BusinessException;
import io.github.springdddtemplate.infrastructure.security.SecurityConfig;
import io.github.springdddtemplate.interfaces.config.WebMvcConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/// UserController MockMVC tests — covers both V1 and V2 API versioning.
///
/// Spring Framework 7 resolves the version from:
///   • Request header:    API-Version: 2
///   • Request parameter: ?api-version=2
///   • Default (omitted): version 1
///
/// WebMvcConfig is imported to activate the versioning infrastructure in the test slice.
@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, WebMvcConfig.class})
@DisplayName("UserController MockMVC Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UserApplicationService userApplicationService;

    // -----------------------------------------------------------------------
    // V1 tests
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("V1 — Create User API")
    class CreateUserV1Tests {

        @Test
        @WithMockUser
        @DisplayName("Should create user (v1) with API-Version header")
        void shouldCreateUserV1WithHeader() throws Exception {
            var request = new CreateUserRequest("john_doe", "securePass123", "john@example.com", "MEMBER");
            var response = new UserResponse(1L, "john_doe", "john@example.com", "Member", true);

            when(userApplicationService.createUser(request)).thenReturn(response);

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/users")
                            .header("API-Version", "1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value("john_doe"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value("john@example.com"));
        }

        @Test
        @WithMockUser
        @DisplayName("Should create user (v1) using default version — no header")
        void shouldCreateUserV1WithDefaultVersion() throws Exception {
            var request = new CreateUserRequest("john_doe", "securePass123", "john@example.com", "MEMBER");
            var response = new UserResponse(1L, "john_doe", "john@example.com", "Member", true);

            when(userApplicationService.createUser(request)).thenReturn(response);

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value("john_doe"));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 400 when request has invalid data (v1)")
        void shouldReturn400ForInvalidData() throws Exception {
            var request = new CreateUserRequest("", "short", "not-an-email", "");

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/users")
                            .header("API-Version", "1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 409 when username already exists (v1)")
        void shouldReturn409ForDuplicateUsername() throws Exception {
            var request = new CreateUserRequest("existing_user", "securePass123", "new@example.com", "MEMBER");

            when(userApplicationService.createUser(request))
                    .thenThrow(BusinessException.business("USER_DUPLICATE_USERNAME",
                            "Username already exists: existing_user"));

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/users")
                            .header("API-Version", "1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(MockMvcResultMatchers.status().isConflict())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value("USER_DUPLICATE_USERNAME"));
        }
    }

    @Nested
    @DisplayName("V1 — Get User API")
    class GetUserV1Tests {

        @Test
        @WithMockUser
        @DisplayName("Should return V1 user when found (header)")
        void shouldReturnUserWhenFoundV1Header() throws Exception {
            var response = new UserResponse(1L, "john_doe", "john@example.com", "Member", true);
            when(userApplicationService.getUser(1L)).thenReturn(response);

            mockMvc.perform(MockMvcRequestBuilders
                            .get("/api/users/1")
                            .header("API-Version", "1"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data.displayName").doesNotExist());
        }

        @Test
        @WithMockUser
        @DisplayName("Should return V1 user when found (query param)")
        void shouldReturnUserWhenFoundV1Param() throws Exception {
            var response = new UserResponse(1L, "john_doe", "john@example.com", "Member", true);
            when(userApplicationService.getUser(1L)).thenReturn(response);

            mockMvc.perform(MockMvcRequestBuilders
                            .get("/api/users/1")
                            .param("api-version", "1"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 404 when user not found (v1)")
        void shouldReturn404WhenUserNotFound() throws Exception {
            when(userApplicationService.getUser(999L))
                    .thenThrow(BusinessException.notFound("USER_NOT_FOUND",
                            "User not found with id: 999"));

            mockMvc.perform(MockMvcRequestBuilders
                            .get("/api/users/999")
                            .header("API-Version", "1"))
                    .andExpect(MockMvcResultMatchers.status().isNotFound())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value("USER_NOT_FOUND"));
        }
    }

    // -----------------------------------------------------------------------
    // V2 tests
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("V2 — Create User API")
    class CreateUserV2Tests {

        @Test
        @WithMockUser
        @DisplayName("Should create user (v2) via header and return enriched response")
        void shouldCreateUserV2WithHeader() throws Exception {
            var request = new CreateUserRequest("jane_doe", "securePass123", "jane@example.com", "ADMIN");
            var v2response = new UserResponseV2(2L, "jane_doe", "jane@example.com", "Administrator",
                    true, "jane_doe", Instant.parse("2026-01-15T10:00:00Z"));

            when(userApplicationService.createUserV2(request)).thenReturn(v2response);

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/users")
                            .header("API-Version", "2")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value("jane_doe"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data.displayName").value("jane_doe"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data.createdAt").exists());
        }

        @Test
        @WithMockUser
        @DisplayName("Should create user (v2) via query param")
        void shouldCreateUserV2WithQueryParam() throws Exception {
            var request = new CreateUserRequest("jane_doe", "securePass123", "jane@example.com", "ADMIN");
            var v2response = new UserResponseV2(2L, "jane_doe", "jane@example.com", "Administrator",
                    true, "jane_doe", Instant.parse("2026-01-15T10:00:00Z"));

            when(userApplicationService.createUserV2(request)).thenReturn(v2response);

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/users")
                            .param("api-version", "2")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data.displayName").value("jane_doe"));
        }
    }

    @Nested
    @DisplayName("V2 — Get User API")
    class GetUserV2Tests {

        @Test
        @WithMockUser
        @DisplayName("Should return V2 user with displayName and createdAt (header)")
        void shouldReturnEnrichedUserV2WithHeader() throws Exception {
            var v2response = new UserResponseV2(1L, "john_doe", "john@example.com", "Member",
                    true, "john_doe", Instant.parse("2026-01-10T08:00:00Z"));
            when(userApplicationService.getUserV2(1L)).thenReturn(v2response);

            mockMvc.perform(MockMvcRequestBuilders
                            .get("/api/users/1")
                            .header("API-Version", "2"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data.displayName").value("john_doe"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data.createdAt").exists());
        }

        @Test
        @WithMockUser
        @DisplayName("Should return V2 user (query param)")
        void shouldReturnEnrichedUserV2WithQueryParam() throws Exception {
            var v2response = new UserResponseV2(1L, "john_doe", "john@example.com", "Member",
                    true, "john_doe", Instant.parse("2026-01-10T08:00:00Z"));
            when(userApplicationService.getUserV2(1L)).thenReturn(v2response);

            mockMvc.perform(MockMvcRequestBuilders
                            .get("/api/users/1")
                            .param("api-version", "2"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data.displayName").value("john_doe"));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 404 when user not found (v2)")
        void shouldReturn404WhenUserNotFoundV2() throws Exception {
            when(userApplicationService.getUserV2(999L))
                    .thenThrow(BusinessException.notFound("USER_NOT_FOUND",
                            "User not found with id: 999"));

            mockMvc.perform(MockMvcRequestBuilders
                            .get("/api/users/999")
                            .header("API-Version", "2"))
                    .andExpect(MockMvcResultMatchers.status().isNotFound())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value("USER_NOT_FOUND"));
        }
    }

    // -----------------------------------------------------------------------
    // Bean Validation Tests (version-neutral)
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("Bean Validation Tests")
    class BeanValidationTests {

        @Test
        @WithMockUser
        @DisplayName("Should reject username with special characters")
        void shouldRejectUsernameWithSpecialChars() throws Exception {
            var request = new CreateUserRequest("john@doe!", "securePass123", "john@example.com", "MEMBER");

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/users")
                            .header("API-Version", "1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value("VALIDATION_FAILED"));
        }

        @Test
        @WithMockUser
        @DisplayName("Should reject username with spaces")
        void shouldRejectUsernameWithSpaces() throws Exception {
            var request = new CreateUserRequest("john doe", "securePass123", "john@example.com", "MEMBER");

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/users")
                            .header("API-Version", "1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("Should reject invalid role")
        void shouldRejectInvalidRole() throws Exception {
            var request = new CreateUserRequest("john_doe", "securePass123", "john@example.com", "SUPERUSER");

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/users")
                            .header("API-Version", "1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value("VALIDATION_FAILED"));
        }

        @Test
        @WithMockUser
        @DisplayName("Should accept case-insensitive role")
        void shouldAcceptCaseInsensitiveRole() throws Exception {
            var request = new CreateUserRequest("john_doe", "securePass123", "john@example.com", "admin");
            var response = new UserResponse(1L, "john_doe", "john@example.com", "Administrator", true);

            when(userApplicationService.createUser(request)).thenReturn(response);

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/users")
                            .header("API-Version", "1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(MockMvcResultMatchers.status().isCreated());
        }

        @Test
        @WithMockUser
        @DisplayName("Should reject short password")
        void shouldRejectShortPassword() throws Exception {
            var request = new CreateUserRequest("john_doe", "short", "john@example.com", "MEMBER");

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/users")
                            .header("API-Version", "1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 400 for negative user ID")
        void shouldReturn400ForNegativeUserId() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                            .get("/api/users/-1")
                            .header("API-Version", "1"))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value("CONSTRAINT_VIOLATION"));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 400 for zero user ID")
        void shouldReturn400ForZeroUserId() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                            .get("/api/users/0")
                            .header("API-Version", "1"))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value("CONSTRAINT_VIOLATION"));
        }
    }

    // -----------------------------------------------------------------------
    // Authentication Tests
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("Authentication Tests")
    class AuthenticationTests {

        @Test
        @DisplayName("Should return 401 when no authentication provided")
        void shouldReturn401WithoutAuth() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                            .get("/api/users/1")
                            .header("API-Version", "1"))
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        }
    }
}
