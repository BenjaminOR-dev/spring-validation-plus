package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.DigitsBetweenValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DigitsBetweenValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DigitsBetween {
    String message() default "{dev.benjaminor.validationplus.constraints.DigitsBetween.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int min();
    int max();
}
