package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.UlidValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UlidValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Ulid {

    String message() default "{dev.benjaminor.validationplus.constraints.Ulid.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
