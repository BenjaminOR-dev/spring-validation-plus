package dev.benjaminor.validationplus.support;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * Utilidades para calcular longitudes de distintos tipos soportados por las validaciones.
 */
public final class LengthUtils {

    private LengthUtils() {
    }

    /**
     * Obtiene la longitud de un valor soportado por {@code @MinLength} y {@code @MaxLength}.
     *
     * @param value valor a medir
     * @return longitud del valor o {@code -1} si el tipo no es soportado
     */
    public static int getLength(Object value) {
        if (value == null) {
            return -1;
        }
        if (value instanceof CharSequence charSequence) {
            return charSequence.length();
        }
        if (value instanceof Collection<?> collection) {
            return collection.size();
        }
        if (value instanceof Map<?, ?> map) {
            return map.size();
        }
        if (value.getClass().isArray()) {
            return Array.getLength(value);
        }
        return -1;
    }
}
