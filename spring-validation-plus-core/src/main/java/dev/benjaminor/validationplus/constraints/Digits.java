package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.DigitsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that a numeric value has the specified number of integer and fractional digits.
 */
@Documented
@Constraint(validatedBy = DigitsValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Digits {

    String message() default "{dev.benjaminor.validationplus.constraints.Digits.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Maximum number of integer digits.
     */
    int integer();

    /**
     * Maximum number of fractional digits.
     */
    int fraction() default 0;
}
