package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.NumericValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that the value is a Java {@link Number}.
 */
@Documented
@Constraint(validatedBy = NumericValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Numeric {

    String message() default "{dev.benjaminor.validationplus.constraints.Numeric.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
