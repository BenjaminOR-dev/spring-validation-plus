package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.MissingValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that the specified field is not present in the payload.
 */
@Documented
@Repeatable(Missing.List.class)
@Constraint(validatedBy = MissingValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Missing {

    String message() default "{dev.benjaminor.validationplus.constraints.Missing.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    /**
     * Field that must be absent.
     */
    String field();

    @Documented
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        Missing[] value();
    }
}
