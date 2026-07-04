package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.DistinctValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DistinctValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Distinct {

    String message() default "{dev.benjaminor.validationplus.constraints.Distinct.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
