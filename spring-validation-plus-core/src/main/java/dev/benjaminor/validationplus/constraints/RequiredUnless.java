package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.RequiredUnlessValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Valida que un campo sea obligatorio a menos que otro campo tenga un valor específico.
 */
@Documented
@Repeatable(RequiredUnless.List.class)
@Constraint(validatedBy = RequiredUnlessValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredUnless {

    String message() default "{dev.benjaminor.validationplus.constraints.RequiredUnless.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Campo observado.
     */
    String field();

    /**
     * Valor que exime la obligatoriedad.
     */
    String value();

    /**
     * Campo que debe estar presente salvo la excepción.
     */
    String required();

    @Documented
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        RequiredUnless[] value();
    }
}
