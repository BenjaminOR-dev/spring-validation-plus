package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.MissingIfValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Repeatable(MissingIf.List.class)
@Constraint(validatedBy = MissingIfValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MissingIf {
    String message() default "{dev.benjaminor.validationplus.constraints.MissingIf.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String field();
    String value();
    String missing();
    @Documented
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List { MissingIf[] value(); }
}
