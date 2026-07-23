package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.MustBeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that the value equals exactly the expected constant
 * (e.g. {@code @MustBe("T")} for a flag that must be accepted).
 *
 * <p>Null is considered valid so it can be combined with {@code @Required}.
 */
@Documented
@Constraint(validatedBy = MustBeValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MustBe {

    String message() default "{dev.benjaminor.validationplus.constraints.MustBe.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Expected value (compared after trim; case-sensitive by default).
     */
    String value();

    /**
     * Whether comparison ignores case.
     */
    boolean ignoreCase() default false;
}
