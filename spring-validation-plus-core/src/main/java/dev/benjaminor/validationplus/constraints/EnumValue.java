package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.EnumValueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that the value matches a constant of the specified enum.
 */
@Documented
@Constraint(validatedBy = EnumValueValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumValue {

    String message() default "{dev.benjaminor.validationplus.constraints.EnumValue.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    /**
     * Enum type containing allowed constants.
     */
    Class<? extends java.lang.Enum<?>> enumClass();

    /**
     * Whether constant name matching ignores case.
     */
    boolean ignoreCase() default false;
}
