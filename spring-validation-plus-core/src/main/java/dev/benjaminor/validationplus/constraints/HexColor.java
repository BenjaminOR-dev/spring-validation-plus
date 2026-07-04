package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.HexColorValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that the value is a hexadecimal color (3 or 6 digits, with optional leading #).
 */
@Documented
@Constraint(validatedBy = HexColorValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HexColor {

    String message() default "{dev.benjaminor.validationplus.constraints.HexColor.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
