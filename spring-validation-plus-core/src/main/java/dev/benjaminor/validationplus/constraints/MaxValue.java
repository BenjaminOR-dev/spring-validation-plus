package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.MaxValueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that a numeric value is less than or equal to the allowed maximum.
 */
@Documented
@Constraint(validatedBy = MaxValueValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MaxValue {

    String message() default "{dev.benjaminor.validationplus.constraints.MaxValue.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Allowed maximum value.
     */
    double value();
}
