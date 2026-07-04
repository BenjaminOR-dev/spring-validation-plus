package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.RequiredWithAllValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that a field is required when all of the specified fields are present.
 */
@Documented
@Repeatable(RequiredWithAll.List.class)
@Constraint(validatedBy = RequiredWithAllValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredWithAll {

    String message() default "{dev.benjaminor.validationplus.constraints.RequiredWithAll.message}";
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
        RequiredWithAll[] value();
    }
}
