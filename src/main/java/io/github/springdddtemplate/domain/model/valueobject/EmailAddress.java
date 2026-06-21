package io.github.springdddtemplate.domain.model.valueobject;

/// Email address value object - using Java record for immutable VO.
/// Records provide compact syntax for immutable data carriers,
/// automatically generating equals, hashCode, toString, and accessors.
public record EmailAddress(String value) {

    public EmailAddress {
        if (value == null || !value.contains("@")) {
            throw new IllegalArgumentException("Invalid email address: " + value);
        }
    }

    /**
     * Flexible constructor body (Java 26 feature) -
     * allows validation before field assignment using compact canonical constructor.
     * The `value` parameter is implicitly assigned to the `this.value` field
     * after the constructor body executes.
     */
}
