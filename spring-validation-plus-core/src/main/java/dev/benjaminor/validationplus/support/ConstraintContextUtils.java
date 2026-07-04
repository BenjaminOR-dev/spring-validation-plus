package dev.benjaminor.validationplus.support;

import jakarta.validation.ConstraintValidatorContext;

/**
 * Utilidades para acceder al contexto de validación en runtime.
 */
public final class ConstraintContextUtils {

    private ConstraintContextUtils() {
    }

    /**
     * Obtiene el bean raíz cuando el motor de validación lo expone (p. ej. Hibernate Validator).
     */
    public static Object getRootBean(ConstraintValidatorContext context) {
        if (context == null) {
            return null;
        }
        try {
            return context.getClass().getMethod("getRootBean").invoke(context);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }
}
