package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.MissingUnlessValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Repeatable(MissingUnless.List.class)
@Constraint(validatedBy = MissingUnlessValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MissingUnless {
    String message() default "{dev.benjaminor.validationplus.constraints.MissingUnless.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String field();
    String value();
    String missing();
    @Documented
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List { MissingUnless[] value(); }
}
