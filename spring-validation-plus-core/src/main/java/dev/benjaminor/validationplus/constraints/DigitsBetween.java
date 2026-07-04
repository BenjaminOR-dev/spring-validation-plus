package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.DigitsBetweenValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that a numeric value has a digit count within the allowed range.
 */
@Documented
@Constraint(validatedBy = DigitsBetweenValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DigitsBetween {

    String message() default "{dev.benjaminor.validationplus.constraints.DigitsBetween.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    /**
     * Minimum number of digits.
     */
    int min();

    /**
     * Maximum number of digits.
     */
    int max();
}
