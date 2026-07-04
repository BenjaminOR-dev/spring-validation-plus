package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.InArrayValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Repeatable(InArray.List.class)
@Constraint(validatedBy = InArrayValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface InArray {
    String message() default "{dev.benjaminor.validationplus.constraints.InArray.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String field();
    String arrayField();

    @Documented
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List { InArray[] value(); }
}
