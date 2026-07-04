package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.InArrayValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that a field value is contained in another array or collection field.
 */
@Documented
@Repeatable(InArray.List.class)
@Constraint(validatedBy = InArrayValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface InArray {

    String message() default "{dev.benjaminor.validationplus.constraints.InArray.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    /**
     * Field whose value is validated.
     */
    String field();

    /**
     * Array or collection field containing allowed values.
     */
    String arrayField();

    @Documented
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        InArray[] value();
    }
}
