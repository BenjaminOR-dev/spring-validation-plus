package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.DateFormatValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateFormatValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateFormat {
    String message() default "{dev.benjaminor.validationplus.constraints.DateFormat.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String format();
}
