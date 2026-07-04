package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.EndsWithValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that the string ends with the specified suffix.
 */
@Documented
@Constraint(validatedBy = EndsWithValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EndsWith {

    String message() default "{dev.benjaminor.validationplus.constraints.EndsWith.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Sufijo requerido.
     */
    String value();
}
