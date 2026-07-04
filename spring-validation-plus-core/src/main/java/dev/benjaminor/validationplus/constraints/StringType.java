package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.StringTypeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Valida que el valor sea una cadena de texto de Java.
 */
@Documented
@Constraint(validatedBy = StringTypeValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface StringType {

    String message() default "{dev.benjaminor.validationplus.constraints.StringType.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
