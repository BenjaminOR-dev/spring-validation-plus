package dev.benjaminor.validationplus.support;

import java.util.Collection;

/**
 * Utilidades para validaciones de tipo.
 */
public final class TypeUtils {

    private TypeUtils() {
    }

    /**
     * Indica si el valor es una cadena de texto de Java.
     */
    public static boolean isStringType(Object value) {
        if (value == null) {
            return true;
        }
        return value instanceof CharSequence;
    }

    /**
     * Indica si el valor es un booleano real de Java.
     */
    public static boolean isBooleanType(Object value) {
        if (value == null) {
            return true;
        }
        return value instanceof Boolean;
    }

    /**
     * Indica si el valor es un arreglo o una colección.
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
