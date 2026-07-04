package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.StartsWithValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Valida que la cadena comience con el prefijo especificado.
 */
@Documented
@Constraint(validatedBy = StartsWithValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface StartsWith {

    String message() default "{dev.benjaminor.validationplus.constraints.StartsWith.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Prefijo requerido.
     */
    String value();
}
