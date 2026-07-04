package dev.benjaminor.validationplus.support;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * Utilidades para evaluar vacíos de forma consistente con las reglas tipo Laravel.
 */
public final class EmptyUtils {

    private EmptyUtils() {
    }

    /**
     * Determina si un valor debe considerarse vacío para {@code @Required}.
     */
    public static boolean isEmpty(Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof CharSequence charSequence) {
            return charSequence.toString().trim().isEmpty();
        }
        if (value instanceof Collection<?> collection) {
            return collection.isEmpty();
        }
        if (value instanceof Map<?, ?> map) {
            return map.isEmpty();
        }
        if (value.getClass().isArray()) {
            return Array.getLength(value) == 0;
        }
        return false;
    }

    /**
     * Determina si una cadena es nula o contiene solo espacios.
     */
    public static boolean isBlank(Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof CharSequence charSequence) {
            return charSequence.toString().trim().isEmpty();
        }
        return false;
    }
}
