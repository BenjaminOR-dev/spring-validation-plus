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
 * Valida que el valor sea una imagen subida con dimensiones opcionales.
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
     * Tamaño máximo en kilobytes. {@code 0} significa sin límite.
     */
    int max() default 0;

    /**
     * Tipos MIME o extensiones permitidas. Vacío usa tipos de imagen comunes.
     */
    String[] mimeTypes() default {};

    /**
     * Ancho mínimo en píxeles. {@code 0} significa sin límite.
     */
    int minWidth() default 0;

    /**
     * Ancho máximo en píxeles. {@code 0} significa sin límite.
     */
    int maxWidth() default 0;

    /**
     * Alto mínimo en píxeles. {@code 0} significa sin límite.
     */
    int minHeight() default 0;

    /**
     * Alto máximo en píxeles. {@code 0} significa sin límite.
     */
    int maxHeight() default 0;
}
