package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.MissingWithValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that a field must be absent when any of the specified fields is present.
 */
@Documented
@Repeatable(MissingWith.List.class)
@Constraint(validatedBy = MissingWithValidator.class)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MissingWith {

    String message() default "{dev.benjaminor.validationplus.constraints.MissingWith.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    /**
     * Observed fields. Shorthand when the annotation is placed on the missing field.
     */
    String[] value() default {};

    /**
     * Observed fields.
     */
    String[] fields() default {};

    /**
     * Field that must be absent. Inferred from the annotated property at field level.
     */
    String missing() default "";

    @Documented
    @Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        MissingWith[] value();
    }
}
