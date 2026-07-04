package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.GtValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = GtValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Gt {

    String message() default "{dev.benjaminor.validationplus.constraints.Gt.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    double value();
}
