package io.github.springdddtemplate.application.mapper;

import io.github.springdddtemplate.application.dto.CreateUserRequest;
import io.github.springdddtemplate.application.dto.UpdateUserRequest;
import io.github.springdddtemplate.application.dto.UserResponse;
import io.github.springdddtemplate.domain.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/// Uses componentModel = "spring" for Spring dependency injection,
/// and unmappedTargetPolicy = IGNORE to skip null fields during updates.
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    /// Map creation request to domain entity.
    /// Password should be encoded separately by the service layer.
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "id", ignore = true)
    User toEntity(CreateUserRequest request);

    /// Map domain entity to response DTO.
    UserResponse toResponse(User user);

    /// Update existing entity from update request.
    /// Only non-null fields will be applied (MapStruct @MappingTarget).
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    void updateEntityFromRequest(UpdateUserRequest request, @MappingTarget User user);
}
