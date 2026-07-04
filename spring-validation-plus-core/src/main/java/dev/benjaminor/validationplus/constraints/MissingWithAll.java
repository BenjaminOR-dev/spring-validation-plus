package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.MissingWithAllValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that a field must be absent when all of the specified fields are present.
 */
@Documented
@Repeatable(MissingWithAll.List.class)
@Constraint(validatedBy = MissingWithAllValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MissingWithAll {

    String message() default "{dev.benjaminor.validationplus.constraints.MissingWithAll.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    /**
     * Observed fields.
     */
    String[] fields();

    /**
     * Field that must be absent.
     */
    String missing();

    @Documented
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        MissingWithAll[] value();
    }
}
