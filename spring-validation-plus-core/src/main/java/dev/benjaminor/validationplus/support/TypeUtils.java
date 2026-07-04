package dev.benjaminor.validationplus.support;

import java.util.Collection;

/**
 * Utilities for validaciones de tipo.
 */
public final class TypeUtils {

    private TypeUtils() {
    }

    /**
     * Returns whether the value is a Java string.
     */
    public static boolean isStringType(Object value) {
        if (value == null) {
            return true;
        }
        return value instanceof CharSequence;
    }

    /**
     * Returns whether the value is a native Java boolean.
     */
    public static boolean isBooleanType(Object value) {
        if (value == null) {
            return true;
        }
        return value instanceof Boolean;
    }

    /**
     * Returns whether the value is an array or collection.
     */
    public static boolean isArrayType(Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof Collection<?>) {
            return true;
        }
        return value.getClass().isArray();
    }
}
