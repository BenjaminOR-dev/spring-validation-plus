package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.NotStartsWithValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that the string does not start with the specified prefix.
 */
@Documented
@Constraint(validatedBy = NotStartsWithValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotStartsWith {

    String message() default "{dev.benjaminor.validationplus.constraints.NotStartsWith.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    /**
     * Disallowed prefix.
     */
    String value();
}
