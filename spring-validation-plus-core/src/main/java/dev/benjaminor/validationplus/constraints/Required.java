package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.RequiredValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Valida que el campo no sea nulo ni vacío.
 */
@Documented
@Constraint(validatedBy = RequiredValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Required {

    String message() default "{dev.benjaminor.validationplus.constraints.Required.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
