package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.ProhibitedIfValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that a field must be absent when another field has a specific value.
 */
@Documented
@Repeatable(ProhibitedIf.List.class)
@Constraint(validatedBy = ProhibitedIfValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ProhibitedIf {

    String message() default "{dev.benjaminor.validationplus.constraints.ProhibitedIf.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    /**
     * Observed field.
     */
    String field();

    /**
     * Value that triggers the prohibition.
     */
    String value();

    /**
     * Field that must be absent.
     */
    String prohibited();

    @Documented
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        ProhibitedIf[] value();
    }
}
