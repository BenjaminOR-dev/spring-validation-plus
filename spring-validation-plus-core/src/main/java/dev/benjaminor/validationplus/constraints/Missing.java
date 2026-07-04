package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.MissingValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Repeatable(Missing.List.class)
@Constraint(validatedBy = MissingValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Missing {
    String message() default "{dev.benjaminor.validationplus.constraints.Missing.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String field();

    @Documented
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List { Missing[] value(); }
}
