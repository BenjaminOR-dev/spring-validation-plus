package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.DifferentValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Valida que dos campos tengan valores distintos.
 */
@Documented
@Repeatable(Different.List.class)
@Constraint(validatedBy = DifferentValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Different {

    String message() default "{dev.benjaminor.validationplus.constraints.Different.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Campo de referencia.
     */
    String field();

    /**
     * Campo que debe ser distinto de {@link #field()}.
     */
    String other();

    @Documented
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        Different[] value();
    }
}
