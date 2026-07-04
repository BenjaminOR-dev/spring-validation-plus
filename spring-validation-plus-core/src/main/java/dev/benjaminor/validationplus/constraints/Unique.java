package dev.benjaminor.validationplus.constraints;

import dev.benjaminor.validationplus.validators.UniqueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Valida que el valor de un campo sea único en la entidad indicada.
 * <p>
 * Requiere una implementación de {@link dev.benjaminor.validationplus.spi.UniquenessChecker}
 * registrada en runtime (p. ej. JPA vía el starter).
 */
@Documented
@Repeatable(Unique.List.class)
@Constraint(validatedBy = UniqueValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Unique {

    String message() default "{dev.benjaminor.validationplus.constraints.Unique.message}";

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
     * Campo/columna de la entidad usado en la consulta de unicidad.
     */
    String column();

    /**
     * Campo del DTO con el identificador a excluir en actualizaciones.
     */
    String excludeField() default "";

    /**
     * Nombre del parámetro de ruta o query del que leer el identificador a excluir
     * (p. ej. {@code "id"} en {@code PUT /users/{id}}). Se usa si {@link #excludeField()} está vacío
     * o su valor en el DTO es {@code null}.
     */
    String excludeParameter() default "";

    /**
     * Campo identificador de la entidad usado para excluir el registro actual.
     */
    String excludeColumn() default "id";

    /**
     * Compara ignorando mayúsculas/minúsculas cuando el valor es textual.
     */
    boolean ignoreCase() default true;

    @Documented
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        Unique[] value();
    }
}
