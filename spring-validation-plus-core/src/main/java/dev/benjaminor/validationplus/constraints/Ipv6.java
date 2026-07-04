package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.Ipv6Validator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = Ipv6Validator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Ipv6 {

    String message() default "{dev.benjaminor.validationplus.constraints.Ipv6.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
