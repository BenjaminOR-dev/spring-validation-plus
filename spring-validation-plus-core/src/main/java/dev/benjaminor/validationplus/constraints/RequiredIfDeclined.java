package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.RequiredIfDeclinedValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that a field is required when another field is declined.
 */
@Documented
@Repeatable(RequiredIfDeclined.List.class)
@Constraint(validatedBy = RequiredIfDeclinedValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredIfDeclined {

    String message() default "{dev.benjaminor.validationplus.constraints.RequiredIfDeclined.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    /**
     * Observed field.
     */
    String field();

    /**
     * Field that becomes required.
     */
    String required();

    @Documented
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        RequiredIfDeclined[] value();
    }
}
