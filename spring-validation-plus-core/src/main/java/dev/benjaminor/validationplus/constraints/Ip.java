package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.IpValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IpValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Ip {

    String message() default "{dev.benjaminor.validationplus.constraints.Ip.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
