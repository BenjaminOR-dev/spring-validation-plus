package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.RequiredIfDeclinedValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Repeatable(RequiredIfDeclined.List.class)
@Constraint(validatedBy = RequiredIfDeclinedValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredIfDeclined {
    String message() default "{dev.benjaminor.validationplus.constraints.RequiredIfDeclined.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String field();
    String required();

    @Documented
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List { RequiredIfDeclined[] value(); }
}
