package dev.benjaminor.validationplus.support;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * Utilities for calculating lengths of validation-supported types.
 */
public final class LengthUtils {

    private LengthUtils() {
    }

    /**
     * Returns the length of a value supported by {@code @MinLength} and {@code @MaxLength}.
     *
     * @param value value to measure
     * @return value length, or {@code -1} if the type is not supported
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
