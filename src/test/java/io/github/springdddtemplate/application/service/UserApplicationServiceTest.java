package io.github.springdddtemplate.application.service;

import io.github.springdddtemplate.application.dto.CreateUserRequest;
import io.github.springdddtemplate.application.dto.UserResponse;
import io.github.springdddtemplate.application.mapper.UserMapper;
import io.github.springdddtemplate.domain.exception.BusinessException;
import io.github.springdddtemplate.domain.exception.ErrorCode;
import io.github.springdddtemplate.domain.model.entity.User;
import io.github.springdddtemplate.domain.publisher.DomainEventPublisher;
import io.github.springdddtemplate.domain.repository.UserRepository;
import io.github.springdddtemplate.domain.service.UserDomainService;
import io.github.springdddtemplate.infrastructure.email.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/// UserApplicationService Mockito test - tests application logic in isolation.
/// Uses @MockitoExtension to enable Mockito injection without Spring context.
/// All dependencies are mocked with @Mock, allowing focused testing
/// of the application service's orchestration logic:
/// - Correct delegation to domain service for validation
/// - Correct DTO mapping via MapStruct
/// - Correct password encoding
/// - Correct email notification triggering
@ExtendWith(MockitoExtension.class)
@DisplayName("UserApplicationService Mockito Tests")
class UserApplicationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDomainService userDomainService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private UserApplicationService userApplicationService;

    @Nested
    @DisplayName("Create User")
    class CreateUserTests {

        @Test
        @DisplayName("Should create user successfully when validation passes")
        void shouldCreateUserSuccessfully() {
            var request = new CreateUserRequest("john_doe", "securePass123", "john@example.com", "MEMBER");

            var entity = User.builder()
                    .username("john_doe")
                    .password("encoded_password")
                    .email("john@example.com")
                    .role("MEMBER")
                    .enabled(true)
                    .build();

            var savedEntity = User.builder()
                    .id(1L)
                    .username("john_doe")
                    .password("encoded_password")
                    .email("john@example.com")
                    .role("Member")
                    .enabled(true)
                    .build();

            var expectedResponse = new UserResponse(1L, "john_doe", "john@example.com", "Member", true);

            // Stub mock behaviors
            when(userMapper.toEntity(request)).thenReturn(entity);
            when(passwordEncoder.encode("securePass123")).thenReturn("encoded_password");
            when(userRepository.save(entity)).thenReturn(savedEntity);
            when(userMapper.toResponse(savedEntity)).thenReturn(expectedResponse);

            // Execute
            var result = userApplicationService.createUser(request);

            // Verify
            assertNotNull(result);
            assertEquals(1L, result.id());
            assertEquals("john_doe", result.username());

            // Verify interactions
            verify(userDomainService).validateNewUser("john_doe", "john@example.com", "MEMBER");
            verify(passwordEncoder).encode("securePass123");
            verify(userRepository).save(entity);
            verify(emailService).sendWelcomeEmail("john@example.com", "john_doe");
        }

        @Test
        @DisplayName("Should throw exception when domain validation fails")
        void shouldThrowWhenValidationFails() {
            var request = new CreateUserRequest("existing_user", "securePass123", "existing@example.com", "MEMBER");

            // Mock domain validation to throw
            org.mockito.Mockito.doThrow(
                    BusinessException.business("USER_DUPLICATE_USERNAME", "Username already exists")
            ).when(userDomainService).validateNewUser("existing_user", "existing@example.com", "MEMBER");

            // Execute and verify
            assertThrows(BusinessException.class, () -> userApplicationService.createUser(request));

            // Verify that no persistence happened after validation failure
            verify(userRepository, org.mockito.Mockito.never()).save(any());
            verify(emailService, org.mockito.Mockito.never()).sendWelcomeEmail(any(), any());
        }
    }

    @Nested
    @DisplayName("Get User")
    class GetUserTests {

        @Test
        @DisplayName("Should return user when found by ID")
        void shouldReturnUserWhenFound() {
            var entity = User.builder()
                    .id(1L).username("john_doe").email("john@example.com")
                    .role("Member").enabled(true).build();
            var expectedResponse = new UserResponse(1L, "john_doe", "john@example.com", "Member", true);

            when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(entity));
            when(userMapper.toResponse(entity)).thenReturn(expectedResponse);

            var result = userApplicationService.getUser(1L);

            assertNotNull(result);
            assertEquals("john_doe", result.username());
        }

        @Test
        @DisplayName("Should throw NotFoundError when user not found")
        void shouldThrowNotFoundWhenUserMissing() {
            when(userRepository.findById(999L)).thenReturn(java.util.Optional.empty());

            var exception = assertThrows(BusinessException.class, () -> userApplicationService.getUser(999L));

            // Pattern matching assertion on sealed ErrorCode
            // Using pattern matching for instanceof to verify the specific ErrorCode variant
            if (exception.getErrorCode() instanceof ErrorCode.NotFoundError notFound) {
                assertEquals("USER_NOT_FOUND", notFound.code());
            }
        }
    }
}
