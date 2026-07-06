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
 * Validates that a field matches its confirmation field.
 */
@Documented
@Repeatable(Confirmed.List.class)
@Constraint(validatedBy = ConfirmedValidator.class)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Confirmed {

    String message() default "{dev.benjaminor.validationplus.constraints.Confirmed.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Reference field. Shorthand when the annotation is placed on the confirmation field.
     */
    String value() default "";

    /**
     * Field that must be confirmed.
     */
    String field() default "";

    /**
     * Confirmation field. Defaults to {@code {field}Confirmation}. Used at class level.
     */
    String confirmation() default "";

    @Documented
    @Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        Confirmed[] value();
    }
}
