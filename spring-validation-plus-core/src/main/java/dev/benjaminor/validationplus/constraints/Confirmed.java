package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.ConfirmedValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Valida que un campo coincida con su campo de confirmación.
 */
@Documented
@Repeatable(Confirmed.List.class)
@Constraint(validatedBy = ConfirmedValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Confirmed {

    String message() default "{dev.benjaminor.validationplus.constraints.Confirmed.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Campo que debe confirmarse.
     */
    String field();

    /**
     * Campo de confirmación. Por defecto se usa {@code {field}Confirmation}.
     */
    String confirmation() default "";

    @Documented
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        Confirmed[] value();
    }
}
