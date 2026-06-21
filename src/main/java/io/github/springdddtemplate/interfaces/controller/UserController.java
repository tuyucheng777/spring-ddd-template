package io.github.springdddtemplate.interfaces.controller;

import io.github.springdddtemplate.application.dto.CreateUserRequest;
import io.github.springdddtemplate.application.dto.UpdateUserRequest;
import io.github.springdddtemplate.application.dto.UserResponse;
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

/// User management REST controller - part of the DDD Interface layer.
/// Responsible for: HTTP request/response mapping, input validation, and delegating to application services.
/// Does NOT contain business logic - all logic resides in domain/application layers.
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    private final UserApplicationService userApplicationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new user", description = "Creates a user with the provided details")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Username or email already exists")
    })
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        var response = userApplicationService.createUser(request);
        return ApiResponse.success(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Returns user details for the given ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ApiResponse<UserResponse> getUser(
            @Parameter(description = "User ID", example = "1")
            @PathVariable @Positive(message = "User ID must be a positive number") Long id) {
        var response = userApplicationService.getUser(id);
        return ApiResponse.success(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a user", description = "Updates user details for the given ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ApiResponse<UserResponse> updateUser(
            @Parameter(description = "User ID", example = "1")
            @PathVariable @Positive(message = "User ID must be a positive number") Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        var response = userApplicationService.updateUser(id, request);
        return ApiResponse.success(response);
    }

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

    @PutMapping("/{id}/activate")
    @Operation(summary = "Activate a user", description = "Activates the user account with the given ID")
    public ApiResponse<UserResponse> activateUser(
            @Parameter(description = "User ID", example = "1")
            @PathVariable @Positive(message = "User ID must be a positive number") Long id) {
        var response = userApplicationService.activateUser(id);
        return ApiResponse.success(response);
    }

    @PutMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate a user", description = "Deactivates the user account with the given ID")
    public ApiResponse<UserResponse> deactivateUser(
            @Parameter(description = "User ID", example = "1")
            @PathVariable @Positive(message = "User ID must be a positive number") Long id) {
        var response = userApplicationService.deactivateUser(id);
        return ApiResponse.success(response);
    }
}
