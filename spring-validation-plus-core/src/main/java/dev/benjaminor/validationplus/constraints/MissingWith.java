package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.MissingWithValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Repeatable(MissingWith.List.class)
@Constraint(validatedBy = MissingWithValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MissingWith {
    String message() default "{dev.benjaminor.validationplus.constraints.MissingWith.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String[] fields();
    String missing();

    @Documented
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List { MissingWith[] value(); }
}
