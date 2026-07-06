package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.RequiredIfAcceptedValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that a field is required when another field is accepted.
 */
@Documented
@Repeatable(RequiredIfAccepted.List.class)
@Constraint(validatedBy = RequiredIfAcceptedValidator.class)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredIfAccepted {

    String message() default "{dev.benjaminor.validationplus.constraints.RequiredIfAccepted.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    /**
     * Observed field. Shorthand when the annotation is placed on the required field.
     */
    String value() default "";

    /**
     * Observed field.
     */
    String field() default "";

    /**
     * Field that becomes required. Inferred from the annotated property at field level.
     */
    String required() default "";

    @Documented
    @Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        RequiredIfAccepted[] value();
    }
}
