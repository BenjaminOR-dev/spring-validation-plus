package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.AfterValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Valida que la fecha sea posterior a otra fecha de referencia.
 */
@Documented
@Constraint(validatedBy = AfterValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface After {

    String message() default "{dev.benjaminor.validationplus.constraints.After.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Fecha de referencia fija.
     */
    String value() default "";

    /**
     * Campo del objeto que contiene la fecha de referencia.
     */
    String field() default "";

    /**
     * Formato de fecha esperado.
     */
    String format() default "";
}
