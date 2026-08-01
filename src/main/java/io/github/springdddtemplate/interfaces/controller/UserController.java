package io.github.springdddtemplate.interfaces.controller;

import io.github.springdddtemplate.application.dto.CreateUserRequest;
import io.github.springdddtemplate.application.dto.UpdateUserRequest;
import io.github.springdddtemplate.application.dto.UserResponse;
import io.github.springdddtemplate.application.dto.UserResponseV2;
import io.github.springdddtemplate.application.service.UserApplicationService;
import io.github.springdddtemplate.interfaces.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/// User management REST controller — supports API versioning via Spring Framework 7's
/// native version attribute on mapping annotations.
///
/// Version resolution (configured in WebMvcConfig):
///   • Request header:    API-Version: 2
///   • Request parameter: ?api-version=2
///   • Default version:   1 (applied when no version is provided)
///
/// Version semantics used here:
///   version = "1"   — matches exactly version 1
///   version = "2+"  — matches version 2 and any higher supported version
///   (no version)    — unversioned fallback, lowest priority, superseded by any versioned match
///
/// V1 endpoints return UserResponse; V2 endpoints return UserResponseV2 (adds displayName, createdAt).
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    private final UserApplicationService userApplicationService;

    // -----------------------------------------------------------------------
    // POST /api/users  — create user
    // -----------------------------------------------------------------------

    @PostMapping(version = "1")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new user (v1)", description = "Creates a user and returns the V1 response")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Username or email already exists")
    })
    public ApiResponse<UserResponse> createUserV1(@Valid @RequestBody CreateUserRequest request) {
        return ApiResponse.success(userApplicationService.createUser(request));
    }

    @PostMapping(version = "2+")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new user (v2+)", description = "Creates a user and returns the V2 response with displayName and createdAt")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Username or email already exists")
    })
    public ApiResponse<UserResponseV2> createUserV2(@Valid @RequestBody CreateUserRequest request) {
        return ApiResponse.success(userApplicationService.createUserV2(request));
    }

    // -----------------------------------------------------------------------
    // GET /api/users/{id}  — get user by ID
    // -----------------------------------------------------------------------

    @GetMapping(value = "/{id}", version = "1")
    @Operation(summary = "Get user by ID (v1)", description = "Returns user details for the given ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ApiResponse<UserResponse> getUserV1(
            @Parameter(description = "User ID", example = "1")
            @PathVariable @Positive(message = "User ID must be a positive number") Long id) {
        return ApiResponse.success(userApplicationService.getUser(id));
    }

    @GetMapping(value = "/{id}", version = "2+")
    @Operation(summary = "Get user by ID (v2+)", description = "Returns enriched user details including displayName and createdAt")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ApiResponse<UserResponseV2> getUserV2(
            @Parameter(description = "User ID", example = "1")
            @PathVariable @Positive(message = "User ID must be a positive number") Long id) {
        return ApiResponse.success(userApplicationService.getUserV2(id));
    }

    // -----------------------------------------------------------------------
    // PUT /api/users/{id}  — update user
    // -----------------------------------------------------------------------

    @PutMapping(value = "/{id}", version = "1")
    @Operation(summary = "Update a user (v1)", description = "Updates user details and returns V1 response")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ApiResponse<UserResponse> updateUserV1(
            @Parameter(description = "User ID", example = "1")
            @PathVariable @Positive(message = "User ID must be a positive number") Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        return ApiResponse.success(userApplicationService.updateUser(id, request));
    }

    @PutMapping(value = "/{id}", version = "2+")
    @Operation(summary = "Update a user (v2+)", description = "Updates user details and returns V2 response with displayName and createdAt")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ApiResponse<UserResponseV2> updateUserV2(
            @Parameter(description = "User ID", example = "1")
            @PathVariable @Positive(message = "User ID must be a positive number") Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        return ApiResponse.success(userApplicationService.updateUserV2(id, request));
    }

    // -----------------------------------------------------------------------
    // DELETE /api/users/{id}  — delete user (version-neutral, no payload change)
    // -----------------------------------------------------------------------

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a user", description = "Deletes the user with the given ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ApiResponse<Void> deleteUser(
            @Parameter(description = "User ID", example = "1")
            @PathVariable @Positive(message = "User ID must be a positive number") Long id) {
        userApplicationService.deleteUser(id);
        return ApiResponse.empty();
    }

    // -----------------------------------------------------------------------
    // PUT /api/users/{id}/activate|deactivate  — status transitions (version-neutral)
    // -----------------------------------------------------------------------

    @PutMapping("/{id}/activate")
    @Operation(summary = "Activate a user", description = "Activates the user account with the given ID")
    public ApiResponse<UserResponse> activateUser(
            @Parameter(description = "User ID", example = "1")
            @PathVariable @Positive(message = "User ID must be a positive number") Long id) {
        return ApiResponse.success(userApplicationService.activateUser(id));
    }

    @PutMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate a user", description = "Deactivates the user account with the given ID")
    public ApiResponse<UserResponse> deactivateUser(
            @Parameter(description = "User ID", example = "1")
            @PathVariable @Positive(message = "User ID must be a positive number") Long id) {
        return ApiResponse.success(userApplicationService.deactivateUser(id));
    }
}
