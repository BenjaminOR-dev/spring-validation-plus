package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.LteValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = LteValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Lte {

    String message() default "{dev.benjaminor.validationplus.constraints.Lte.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    double value();
}
