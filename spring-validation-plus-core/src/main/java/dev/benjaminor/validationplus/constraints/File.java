package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.FileValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Valida que el valor sea un archivo subido ({@code MultipartFile}, {@code Part} o {@link dev.benjaminor.validationplus.support.UploadHandle}).
 */
@Documented
@Constraint(validatedBy = FileValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface File {

    String message() default "{dev.benjaminor.validationplus.constraints.File.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Tamaño máximo en kilobytes. {@code 0} significa sin límite.
     */
    int max() default 0;

    /**
     * Tipos MIME o extensiones permitidas (p. ej. {@code image/png}, {@code pdf}).
     */
    String[] mimeTypes() default {};

    /**
     * Extensiones permitidas sin punto (p. ej. {@code pdf}, {@code jpg}).
     */
    String[] extensions() default {};
}
