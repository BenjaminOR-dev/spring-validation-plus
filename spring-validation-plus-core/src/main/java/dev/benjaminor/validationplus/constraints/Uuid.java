package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.UuidValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that the value is a valid UUID.
 */
@Documented
@Constraint(validatedBy = UuidValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Uuid {

    String message() default "{dev.benjaminor.validationplus.constraints.Uuid.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
