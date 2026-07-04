package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.NotStartsWithValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NotStartsWithValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotStartsWith {
    String message() default "{dev.benjaminor.validationplus.constraints.NotStartsWith.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String value();
}
