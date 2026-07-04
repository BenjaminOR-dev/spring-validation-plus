package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.ImageValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates that the value is an uploaded image with optional dimension constraints.
 */
@Documented
@Constraint(validatedBy = ImageValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Image {

    String message() default "{dev.benjaminor.validationplus.constraints.Image.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Maximum size in kilobytes. {@code 0} means no limit.
     */
    int max() default 0;

    /**
     * Allowed MIME types or extensions. Empty uses common image types.
     */
    String[] mimeTypes() default {};

    /**
     * Minimum width in pixels. {@code 0} means no limit.
     */
    int minWidth() default 0;

    /**
     * Maximum width in pixels. {@code 0} means no limit.
     */
    int maxWidth() default 0;

    /**
     * Minimum height in pixels. {@code 0} means no limit.
     */
    int minHeight() default 0;

    /**
     * Maximum height in pixels. {@code 0} means no limit.
     */
    int maxHeight() default 0;
}
