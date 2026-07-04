package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.MinValueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that a numeric value is greater than or equal to the allowed minimum.
 */
@Documented
@Constraint(validatedBy = MinValueValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MinValue {

    String message() default "{dev.benjaminor.validationplus.constraints.MinValue.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Allowed minimum value.
     */
    double value();
}
