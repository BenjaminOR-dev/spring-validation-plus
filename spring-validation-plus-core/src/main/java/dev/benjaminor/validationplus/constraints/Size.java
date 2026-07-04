package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.SizeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that the size of strings, collections, maps, or arrays is exactly the specified value.
 */
@Documented
@Constraint(validatedBy = SizeValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Size {

    String message() default "{dev.benjaminor.validationplus.constraints.Size.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Exact required size.
     */
    int value();
}
