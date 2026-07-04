package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.ActiveUrlValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that the value is a valid HTTP or HTTPS URL with a host.
 */
@Documented
@Constraint(validatedBy = ActiveUrlValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ActiveUrl {

    String message() default "{dev.benjaminor.validationplus.constraints.ActiveUrl.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
