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
 * Validates that a field value exists in the specified entity.
 * <p>
 * Requires an {@link dev.benjaminor.validationplus.spi.ExistenceChecker} implementation
 * registered at runtime (for example, JPA via the starter).
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
     * JPA entity or domain object queried by the checker.
     */
    Class<?> entity();

    /**
     * DTO field whose value is validated and where the error is reported.
     */
    String field();

    /**
     * Entity field/column used in the existence query.
     */
    String column();

    /**
     * Path or query parameter name used to read the value to validate when the
     * DTO field is empty (for example, {@code "roleId"} in {@code /users/{roleId}/posts}).
     */
    String parameter() default "";

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
        Exists[] value();
    }
}
