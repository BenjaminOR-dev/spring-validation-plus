package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {
    String message() default "{dev.benjaminor.validationplus.constraints.Password.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int min() default 8;
    boolean letters() default false;
    boolean mixedCase() default false;
    boolean numbers() default false;
    boolean symbols() default false;
}
