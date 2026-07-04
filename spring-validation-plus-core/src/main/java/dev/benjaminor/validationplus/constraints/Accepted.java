package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.AcceptedValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Valida que el valor sea aceptado (por ejemplo, un booleano verdadero o una cadena "yes"/"on"/"1").
 */
@Documented
@Constraint(validatedBy = AcceptedValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Accepted {

    String message() default "{dev.benjaminor.validationplus.constraints.Accepted.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
