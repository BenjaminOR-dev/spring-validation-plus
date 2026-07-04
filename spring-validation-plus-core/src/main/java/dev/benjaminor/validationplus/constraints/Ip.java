package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.IpValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that the value is a valid IPv4 or IPv6 address.
 */
@Documented
@Constraint(validatedBy = IpValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Ip {

    String message() default "{dev.benjaminor.validationplus.constraints.Ip.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
