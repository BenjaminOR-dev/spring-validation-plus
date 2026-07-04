package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.NotRegexValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Valida que el valor no coincida con la expresión regular especificada.
 */
@Documented
@Constraint(validatedBy = NotRegexValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotRegex {

    String message() default "{dev.benjaminor.validationplus.constraints.NotRegex.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Patrón de la expresión regular.
     */
    String pattern();

    /**
     * Banderas de compilación de la expresión regular.
     */
    int flags() default 0;
}
