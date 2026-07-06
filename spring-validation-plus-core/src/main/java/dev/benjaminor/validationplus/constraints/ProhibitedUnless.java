package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.ProhibitedUnlessValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that a field must be absent unless another field has a specific value.
 */
@Documented
@Repeatable(ProhibitedUnless.List.class)
@Constraint(validatedBy = ProhibitedUnlessValidator.class)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ProhibitedUnless {

    String message() default "{dev.benjaminor.validationplus.constraints.ProhibitedUnless.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    /**
     * Observed field.
     */
    String field();

    /**
     * Value that exempts the prohibition.
     */
    String value();

    /**
     * How {@link #field()} is compared to {@link #value()}. Defaults to {@link ConditionalOperator#EQUALS}.
     */
    ConditionalOperator operator() default ConditionalOperator.EQUALS;

    /**
     * Field that must be absent except for the exemption. Inferred from the annotated property at field level.
     */
    String prohibited() default "";

    @Documented
    @Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        ProhibitedUnless[] value();
    }
}
