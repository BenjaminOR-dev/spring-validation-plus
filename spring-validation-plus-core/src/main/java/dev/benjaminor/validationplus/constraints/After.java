package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.AfterValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that the date is after another reference date.
 */
@Documented
@Constraint(validatedBy = AfterValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface After {

    String message() default "{dev.benjaminor.validationplus.constraints.After.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Fixed reference date.
     */
    String value() default "";

    /**
     * Object field containing the reference date.
     */
    String field() default "";

    /**
     * Expected date format.
     */
    String format() default "";
}
