package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.AlphaNumValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AlphaNumValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AlphaNum {

    String message() default "{dev.benjaminor.validationplus.constraints.AlphaNum.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
