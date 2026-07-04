package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.MultipleOfValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that a numeric value is a multiple of the specified divisor.
 */
@Documented
@Constraint(validatedBy = MultipleOfValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MultipleOf {

    String message() default "{dev.benjaminor.validationplus.constraints.MultipleOf.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Divisor the value must be a multiple of.
     */

    double value();
}
