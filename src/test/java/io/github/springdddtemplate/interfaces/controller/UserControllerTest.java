package io.github.springdddtemplate.interfaces.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.springdddtemplate.application.dto.CreateUserRequest;
import io.github.springdddtemplate.application.dto.UserResponse;
import io.github.springdddtemplate.application.service.UserApplicationService;
import io.github.springdddtemplate.domain.exception.BusinessException;
import io.github.springdddtemplate.infrastructure.security.SecurityConfig;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/// UserController MockMVC test - tests REST API endpoints in isolation.
/// Uses @WebMvcTest to load only the web layer (controllers, filters, etc.)
/// while mocking the application service layer with Mockito.
///
/// @WithMockUser provides test authentication for secured endpoints.
/// MockBean replaces the real UserApplicationService with a Mockito mock.
@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
@DisplayName("UserController MockMVC Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UserApplicationService userApplicationService;

    @Nested
    @DisplayName("Create User API")
    class CreateUserTests {

        @Test
        @WithMockUser
        @DisplayName("Should create user successfully with valid data")
        void shouldCreateUserSuccessfully() throws Exception {
            var request = new CreateUserRequest("john_doe", "securePass123", "john@example.com", "MEMBER");
            var response = new UserResponse(1L, "john_doe", "john@example.com", "Member", true);

            // Using Mockito's when/thenReturn for mock behavior
            when(userApplicationService.createUser(request))
                    .thenReturn(response);

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers
                            .status().isCreated())
                    .andExpect(MockMvcResultMatchers
                            .jsonPath("$.success").value(true))
                    .andExpect(MockMvcResultMatchers
                            .jsonPath("$.data.username").value("john_doe"))
                    .andExpect(MockMvcResultMatchers
                            .jsonPath("$.data.email").value("john@example.com"));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 400 when request has invalid data")
        void shouldReturn400ForInvalidData() throws Exception {
            // Empty username and invalid email
            var request = new CreateUserRequest("", "short", "not-an-email", "");

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(MockMvcResultMatchers
                            .status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 409 when username already exists")
        void shouldReturn409ForDuplicateUsername() throws Exception {
            var request = new CreateUserRequest("existing_user", "securePass123", "new@example.com", "MEMBER");

            when(userApplicationService.createUser(request))
                    .thenThrow(BusinessException.business("USER_DUPLICATE_USERNAME",
                            "Username already exists: existing_user"));

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(MockMvcResultMatchers
                            .status().isConflict())
                    .andExpect(MockMvcResultMatchers
                            .jsonPath("$.errorCode").value("USER_DUPLICATE_USERNAME"));
        }
    }

    @Nested
    @DisplayName("Get User API")
    class GetUserTests {

        @Test
        @WithMockUser
        @DisplayName("Should return user when found")
        void shouldReturnUserWhenFound() throws Exception {
            var response = new UserResponse(1L, "john_doe", "john@example.com", "Member", true);
            when(userApplicationService.getUser(1L)).thenReturn(response);

            mockMvc.perform(MockMvcRequestBuilders
                            .get("/api/users/1"))
                    .andExpect(MockMvcResultMatchers
                            .status().isOk())
                    .andExpect(MockMvcResultMatchers
                            .jsonPath("$.data.id").value(1));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 404 when user not found")
        void shouldReturn404WhenUserNotFound() throws Exception {
            when(userApplicationService.getUser(999L))
                    .thenThrow(BusinessException.notFound("USER_NOT_FOUND",
                            "User not found with id: 999"));

            mockMvc.perform(MockMvcRequestBuilders
                            .get("/api/users/999"))
                    .andExpect(MockMvcResultMatchers
                            .status().isNotFound())
                    .andExpect(MockMvcResultMatchers
                            .jsonPath("$.errorCode").value("USER_NOT_FOUND"));
        }
    }

    @Nested
    @DisplayName("Authentication Tests")
    class AuthenticationTests {

        @Test
        @DisplayName("Should return 401 when no authentication provided")
        void shouldReturn401WithoutAuth() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                            .get("/api/users/1"))
                    .andExpect(MockMvcResultMatchers
                            .status().isUnauthorized());
        }
    }
}
