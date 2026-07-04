package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.FilledValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FilledValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Filled {

    String message() default "{dev.benjaminor.validationplus.constraints.Filled.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
