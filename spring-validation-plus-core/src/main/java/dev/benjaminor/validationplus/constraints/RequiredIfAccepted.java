package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.RequiredIfAcceptedValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Repeatable(RequiredIfAccepted.List.class)
@Constraint(validatedBy = RequiredIfAcceptedValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredIfAccepted {
    String message() default "{dev.benjaminor.validationplus.constraints.RequiredIfAccepted.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String field();
    String required();

    @Documented
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List { RequiredIfAccepted[] value(); }
}
