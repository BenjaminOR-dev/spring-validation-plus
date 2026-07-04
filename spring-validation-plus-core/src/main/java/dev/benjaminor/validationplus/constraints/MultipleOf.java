package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.MultipleOfValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MultipleOfValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MultipleOf {

    String message() default "{dev.benjaminor.validationplus.constraints.MultipleOf.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    double value();
}
