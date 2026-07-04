package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.EmailAddressValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Valida que el valor sea una dirección de correo electrónico válida.
 */
@Documented
@Constraint(validatedBy = EmailAddressValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailAddress {

    String message() default "{dev.benjaminor.validationplus.constraints.EmailAddress.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
