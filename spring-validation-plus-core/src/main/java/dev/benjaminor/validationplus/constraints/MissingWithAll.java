package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.MissingWithAllValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Repeatable(MissingWithAll.List.class)
@Constraint(validatedBy = MissingWithAllValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MissingWithAll {
    String message() default "{dev.benjaminor.validationplus.constraints.MissingWithAll.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String[] fields();
    String missing();

    @Documented
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List { MissingWithAll[] value(); }
}
