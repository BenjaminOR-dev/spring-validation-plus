package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.ProhibitedValidator;
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
@Repeatable(Prohibited.List.class)
@Constraint(validatedBy = ProhibitedValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Prohibited {

    String message() default "{dev.benjaminor.validationplus.constraints.Prohibited.message}";
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
        Prohibited[] value();
    }
}
