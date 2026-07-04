package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.BeforeOrEqualValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BeforeOrEqualValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BeforeOrEqual {
    String message() default "{dev.benjaminor.validationplus.constraints.BeforeOrEqual.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String value() default "";
    String field() default "";
    String format() default "";
}
