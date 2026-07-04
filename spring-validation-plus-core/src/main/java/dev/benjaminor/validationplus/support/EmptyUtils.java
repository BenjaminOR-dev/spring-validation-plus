package dev.benjaminor.validationplus.support;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * Utilities for evaluating emptiness consistently with Laravel-style rules.
 */
public final class EmptyUtils {

    private EmptyUtils() {
    }

    /**
     * Determines whether a value should be considered empty for {@code @Required}.
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
     * Determines whether a string is null or contains only whitespace.
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
