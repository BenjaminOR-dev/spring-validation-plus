package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.RequiredWithoutAllValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that a field is required when all of the specified fields are missing.
 */
@Documented
@Repeatable(RequiredWithoutAll.List.class)
@Constraint(validatedBy = RequiredWithoutAllValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredWithoutAll {

    String message() default "{dev.benjaminor.validationplus.constraints.RequiredWithoutAll.message}";
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
        RequiredWithoutAll[] value();
    }
}
