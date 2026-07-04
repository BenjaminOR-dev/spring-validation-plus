package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.GteValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that a numeric value is greater than or equal to the specified threshold.
 */
@Documented
@Constraint(validatedBy = GteValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Gte {

    String message() default "{dev.benjaminor.validationplus.constraints.Gte.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Minimum allowed value.
     */

    double value();
}
