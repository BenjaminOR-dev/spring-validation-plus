package dev.benjaminor.validationplus.support;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Utilidades para validaciones de arreglos y colecciones.
 */
public final class ArrayValidationUtils {

    private ArrayValidationUtils() {
    }

    public static boolean isDistinct(Object value) {
        if (value == null) {
            return true;
        }
        Set<Object> seen = new HashSet<>();
        if (value instanceof Collection<?> collection) {
            for (Object item : collection) {
                if (!seen.add(normalize(item))) {
                    return false;
                }
            }
            return true;
        }
        if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            for (int index = 0; index < length; index++) {
                if (!seen.add(normalize(Array.get(value, index)))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean isInArray(Object fieldValue, Object arrayValue) {
        if (fieldValue == null) {
            return true;
        }
        if (arrayValue == null) {
            return false;
        }
        Object normalizedFieldValue = normalize(fieldValue);
        if (arrayValue instanceof Collection<?> collection) {
            for (Object item : collection) {
                if (Objects.equals(normalizedFieldValue, normalize(item))) {
                    return true;
                }
            }
            return false;
        }
        if (arrayValue.getClass().isArray()) {
            int length = Array.getLength(arrayValue);
            for (int index = 0; index < length; index++) {
                if (Objects.equals(normalizedFieldValue, normalize(Array.get(arrayValue, index)))) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private static Object normalize(Object value) {
        if (value instanceof CharSequence charSequence) {
            return charSequence.toString();
        }
        return value;
    }
}
