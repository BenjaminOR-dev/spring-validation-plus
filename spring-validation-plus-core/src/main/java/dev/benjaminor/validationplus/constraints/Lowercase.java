package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.LowercaseValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that the string is entirely lowercase.
 */
@Documented
@Constraint(validatedBy = LowercaseValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Lowercase {

    String message() default "{dev.benjaminor.validationplus.constraints.Lowercase.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
