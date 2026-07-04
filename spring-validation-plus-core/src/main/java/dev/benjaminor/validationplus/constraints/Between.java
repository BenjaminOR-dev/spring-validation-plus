package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.BetweenValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Valida que un valor numérico esté dentro del rango permitido (inclusive).
 */
@Documented
@Constraint(validatedBy = BetweenValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Between {

    String message() default "{dev.benjaminor.validationplus.constraints.Between.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Valor mínimo del rango.
     */
    double min();

    /**
     * Valor máximo del rango.
     */
    double max();
}
