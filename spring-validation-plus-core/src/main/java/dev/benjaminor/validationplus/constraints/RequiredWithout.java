package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.RequiredWithoutValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that a field is required when any of the specified fields is missing.
 */
@Documented
@Repeatable(RequiredWithout.List.class)
@Constraint(validatedBy = RequiredWithoutValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredWithout {

    String message() default "{dev.benjaminor.validationplus.constraints.RequiredWithout.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Observed fields.
     */
    String[] fields();

    /**
     * Field that becomes required.
     */
    String required();

    @Documented
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        RequiredWithout[] value();
    }
}
