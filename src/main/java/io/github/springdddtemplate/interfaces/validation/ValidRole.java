package io.github.springdddtemplate.interfaces.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Custom Bean Validation constraint for user role values.
/// Validates that the role string matches one of the sealed UserRole variants:
/// ADMIN, MEMBER, or GUEST.
/// This bridges the domain's sealed hierarchy with the interface layer's
/// input validation, ensuring invalid roles are rejected before reaching domain logic.
@Documented
@Constraint(validatedBy = RoleValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRole {

    String message() default "Role must be one of: ADMIN, MEMBER, GUEST";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
