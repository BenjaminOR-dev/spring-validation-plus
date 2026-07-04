package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that the value meets configurable password strength rules.
 */
@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {

    String message() default "{dev.benjaminor.validationplus.constraints.Password.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    /**
     * Minimum password length.
     */
    int min() default 8;

    /**
     * Whether at least one letter is required.
     */
    boolean letters() default false;

    /**
     * Whether both uppercase and lowercase letters are required.
     */
    boolean mixedCase() default false;

    /**
     * Whether at least one digit is required.
     */
    boolean numbers() default false;

    /**
     * Whether at least one symbol is required.
     */
    boolean symbols() default false;
}
