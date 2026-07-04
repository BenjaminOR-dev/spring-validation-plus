package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.NullableValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Documenta que el campo puede ser {@code null}. Nunca falla la validación.
 */
@Documented
@Constraint(validatedBy = NullableValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Nullable {

    String message() default "{dev.benjaminor.validationplus.constraints.Nullable.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
