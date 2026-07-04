package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.NotInValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that the value is not included in the forbidden values list.
 */
@Documented
@Constraint(validatedBy = NotInValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotIn {

    String message() default "{dev.benjaminor.validationplus.constraints.NotIn.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Valores prohibidos.
     */
    String[] value();
}
