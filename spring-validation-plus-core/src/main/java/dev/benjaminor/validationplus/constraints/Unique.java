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
 * Validates that a field value is unique in the specified entity.
 * <p>
 * Requires a {@link dev.benjaminor.validationplus.spi.UniquenessChecker} implementation
 * registered at runtime (for example, JPA via the starter).
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
     * JPA entity or domain object queried by the checker.
     */
    Class<?> entity();

    /**
     * DTO field whose value is validated and where the error is reported.
     */
    String field();

    /**
     * Entity field/column used in the uniqueness query.
     */
    String column();

    /**
     * DTO field containing the identifier to exclude on updates.
     */
    String excludeField() default "";

    /**
     * Path or query parameter name used to read the identifier to exclude
     * (for example, {@code "id"} in {@code PUT /users/{id}}). Used when {@link #excludeField()} is empty
     * or its DTO value is {@code null}.
     */
    String excludeParameter() default "";

    /**
     * Entity identifier field used to exclude the current record.
     */
    String excludeColumn() default "id";

    /**
     * Compares case-insensitively when the value is textual.
     */
    boolean ignoreCase() default true;

    /**
     * JPA persistence unit name. Empty uses the primary / only
     * {@code EntityManagerFactory} (backward compatible).
     */
    String persistenceUnit() default "";

    @Documented
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        Unique[] value();
    }
}
