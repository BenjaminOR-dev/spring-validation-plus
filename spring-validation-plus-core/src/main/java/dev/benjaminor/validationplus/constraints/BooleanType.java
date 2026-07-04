package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.BooleanTypeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Valida que el valor sea un booleano real de Java.
 */
@Documented
@Constraint(validatedBy = BooleanTypeValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BooleanType {

    String message() default "{dev.benjaminor.validationplus.constraints.BooleanType.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
