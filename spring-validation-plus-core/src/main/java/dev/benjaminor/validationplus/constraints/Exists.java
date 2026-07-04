package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.ExistsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Valida que el valor de un campo exista en la entidad indicada.
 * <p>
 * Requiere una implementación de {@link dev.benjaminor.validationplus.spi.ExistenceChecker}
 * registrada en runtime (p. ej. JPA vía el starter).
 */
@Documented
@Repeatable(Exists.List.class)
@Constraint(validatedBy = ExistsValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Exists {

    String message() default "{dev.benjaminor.validationplus.constraints.Exists.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Entidad JPA u objeto de dominio consultado por el checker.
     */
    Class<?> entity();

    /**
     * Campo del DTO cuyo valor se valida y donde se reporta el error.
     */
    String field();

    /**
     * Campo/columna de la entidad usado en la consulta de existencia.
     */
    String column();

    /**
     * Nombre del parámetro de ruta o query del que leer el valor a validar cuando el
     * campo del DTO está vacío (p. ej. {@code "roleId"} en {@code /users/{roleId}/posts}).
     */
    String parameter() default "";

    /**
     * Compara ignorando mayúsculas/minúsculas cuando el valor es textual.
     */
    boolean ignoreCase() default true;

    @Documented
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        Exists[] value();
    }
}
