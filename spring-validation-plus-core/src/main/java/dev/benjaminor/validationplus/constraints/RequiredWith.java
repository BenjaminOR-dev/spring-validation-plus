package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.RequiredWithValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that a field is required when any of the specified fields is present.
 * <p>
 * Class level: {@code @RequiredWith(fields = {"password"}, required = "passwordConfirmation")}
 * <p>
 * Field level: {@code @RequiredWith("password") private String passwordConfirmation;}
 */
@Documented
@Repeatable(RequiredWith.List.class)
@Constraint(validatedBy = RequiredWithValidator.class)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredWith {

    String message() default "{dev.benjaminor.validationplus.constraints.RequiredWith.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Observed fields. Shorthand when the annotation is placed on the required field.
     */
    String[] value() default {};

    /**
     * Observed fields.
     */
    String[] fields() default {};

    /**
     * Field that becomes required. Inferred from the annotated property at field level.
     */
    String required() default "";

    @Documented
    @Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        RequiredWith[] value();
    }
}
