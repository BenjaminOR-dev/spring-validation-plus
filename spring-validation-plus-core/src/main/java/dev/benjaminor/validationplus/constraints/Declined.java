package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.DeclinedValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Valida que el valor sea rechazado (por ejemplo, un booleano falso o una cadena "no"/"off"/"0").
 */
@Documented
@Constraint(validatedBy = DeclinedValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Declined {

    String message() default "{dev.benjaminor.validationplus.constraints.Declined.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
