package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.InValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Valida que el valor esté incluido en la lista de valores permitidos.
 */
@Documented
@Constraint(validatedBy = InValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface In {

    String message() default "{dev.benjaminor.validationplus.constraints.In.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Valores permitidos.
     */
    String[] value();
}
