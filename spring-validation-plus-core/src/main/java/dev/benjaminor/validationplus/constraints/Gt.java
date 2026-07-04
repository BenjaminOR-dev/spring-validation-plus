package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.GtValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that a numeric value is greater than the specified threshold.
 */
@Documented
@Constraint(validatedBy = GtValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Gt {

    String message() default "{dev.benjaminor.validationplus.constraints.Gt.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Value that must be exceeded.
     */

    double value();
}
