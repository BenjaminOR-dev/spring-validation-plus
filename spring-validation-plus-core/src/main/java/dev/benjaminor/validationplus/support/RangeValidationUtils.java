package dev.benjaminor.validationplus.support;

import java.math.BigDecimal;

/**
 * Utilities for {@code @Between} and {@code @Size}.
 */
public final class RangeValidationUtils {

    private RangeValidationUtils() {
    }

    public static boolean isBetween(Object value, double min, double max) {
        if (value == null) {
            return true;
        }
        if (value instanceof Number number) {
            BigDecimal current = NumberUtils.toBigDecimal(number);
            return current.compareTo(BigDecimal.valueOf(min)) >= 0
                    && current.compareTo(BigDecimal.valueOf(max)) <= 0;
        }
        if (value instanceof CharSequence charSequence) {
            int length = charSequence.length();
            return length >= min && length <= max;
        }
        int size = measurableSize(value);
        if (size < 0) {
            return false;
        }
        return size >= min && size <= max;
    }

    public static boolean hasExactSize(Object value, int expectedSize) {
        if (value == null) {
            return true;
        }
        if (value instanceof Number number) {
            return NumberUtils.toBigDecimal(number).compareTo(BigDecimal.valueOf(expectedSize)) == 0;
        }
        if (value instanceof CharSequence charSequence) {
            return charSequence.length() == expectedSize;
        }
        int size = measurableSize(value);
        if (size < 0) {
            return false;
        }
        return size == expectedSize;
    }

    private static int measurableSize(Object value) {
        int length = LengthUtils.getLength(value);
        return length >= 0 ? length : -1;
    }
}
