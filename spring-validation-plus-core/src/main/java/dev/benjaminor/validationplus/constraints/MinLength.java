package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.MinLengthValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates the minimum length of strings, collections, maps, or arrays.
 */
@Documented
@Constraint(validatedBy = MinLengthValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MinLength {

    String message() default "{dev.benjaminor.validationplus.constraints.MinLength.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Minimum allowed length.
     */
    int value();
}
