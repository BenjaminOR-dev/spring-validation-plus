package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.MaxDigitsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that a numeric value does not exceed the maximum digit count.
 */
@Documented
@Constraint(validatedBy = MaxDigitsValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MaxDigits {

    String message() default "{dev.benjaminor.validationplus.constraints.MaxDigits.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Maximum number of digits.
     */

    int value();
}
