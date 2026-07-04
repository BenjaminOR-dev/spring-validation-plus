package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.AlphaNumValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that the value contains only Unicode letters and numbers.
 */
@Documented
@Constraint(validatedBy = AlphaNumValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AlphaNum {

    String message() default "{dev.benjaminor.validationplus.constraints.AlphaNum.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
