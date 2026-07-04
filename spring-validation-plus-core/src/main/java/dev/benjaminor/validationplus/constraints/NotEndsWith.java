package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.NotEndsWithValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that the string does not end with the specified suffix.
 */
@Documented
@Constraint(validatedBy = NotEndsWithValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotEndsWith {

    String message() default "{dev.benjaminor.validationplus.constraints.NotEndsWith.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    /**
     * Disallowed suffix.
     */
    String value();
}
