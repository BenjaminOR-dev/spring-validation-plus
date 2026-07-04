package dev.benjaminor.validationplus.support;

import java.util.Arrays;
import java.util.Objects;

/**
 * Utilities for {@code @In} y {@code @NotIn}.
 */
public final class InValidationUtils {

    private InValidationUtils() {
    }

    public static boolean isIn(Object value, String[] allowedValues) {
        if (value == null) {
            return true;
        }
        if (allowedValues == null || allowedValues.length == 0) {
            return false;
        }
        String normalized = normalize(value);
        return Arrays.stream(allowedValues).anyMatch(candidate -> Objects.equals(normalize(candidate), normalized));
    }

    public static boolean isNotIn(Object value, String[] forbiddenValues) {
        if (value == null) {
            return true;
        }
        return !isIn(value, forbiddenValues);
    }

    private static String normalize(Object value) {
        return String.valueOf(value).trim();
    }
}
