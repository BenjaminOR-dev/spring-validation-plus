package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.LtValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that a numeric value is less than the specified threshold.
 */
@Documented
@Constraint(validatedBy = LtValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Lt {

    String message() default "{dev.benjaminor.validationplus.constraints.Lt.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Value that must not be exceeded.
     */

    double value();
}
