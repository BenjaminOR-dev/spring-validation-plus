package dev.benjaminor.validationplus.support;

import jakarta.validation.ConstraintValidatorContext;

/**
 * Utilities for accessing the validation context at runtime.
 */
public final class ConstraintContextUtils {

    private ConstraintContextUtils() {
    }

    /**
     * Returns the root bean when the validation engine exposes it (for example, Hibernate Validator).
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
