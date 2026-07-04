package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.TimezoneValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TimezoneValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Timezone {

    String message() default "{dev.benjaminor.validationplus.constraints.Timezone.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
