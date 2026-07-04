package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.AlphaValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AlphaValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Alpha {

    String message() default "{dev.benjaminor.validationplus.constraints.Alpha.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
