package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.ProhibitedValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Repeatable(Prohibited.List.class)
@Constraint(validatedBy = ProhibitedValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Prohibited {
    String message() default "{dev.benjaminor.validationplus.constraints.Prohibited.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String field();

    @Documented
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List { Prohibited[] value(); }
}
