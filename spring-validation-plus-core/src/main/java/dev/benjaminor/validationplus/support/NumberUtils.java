package dev.benjaminor.validationplus.support;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Utilities for numeric validations.
 */
public final class NumberUtils {

    private NumberUtils() {
    }

    /**
     * Returns whether the value represents a Java integer (not text or decimal).
     */
    public static boolean isIntegerType(Object value) {
        if (value == null) {
            return true;
        }
        return value instanceof Byte
                || value instanceof Short
                || value instanceof Integer
                || value instanceof Long
                || value instanceof BigInteger;
    }

    /**
     * Returns whether the value is a Java {@link Number}.
     */
    public static boolean isNumeric(Object value) {
        if (value == null) {
            return true;
        }
        return value instanceof Number;
    }

    /**
     * Returns whether the value is a Java decimal ({@link Float}, {@link Double}, or {@link BigDecimal}).
     */
    public static boolean isDecimalType(Object value) {
        if (value == null) {
            return true;
        }
        return value instanceof Float
                || value instanceof Double
                || value instanceof BigDecimal;
    }

    /**
     * Returns whether the numeric value is greater than or equal to the allowed minimum.
     */
    public static boolean isMinValue(Number value, Number min) {
        if (value == null) {
            return true;
        }
        return toBigDecimal(value).compareTo(toBigDecimal(min)) >= 0;
    }

    /**
     * Returns whether the numeric value is less than or equal to the allowed maximum.
     */
    public static boolean isMaxValue(Number value, Number max) {
        if (value == null) {
            return true;
        }
        return toBigDecimal(value).compareTo(toBigDecimal(max)) <= 0;
    }

    /**
     * Converts a {@link Number} to {@link BigDecimal} for safe comparisons.
     */
    public static BigDecimal toBigDecimal(Number number) {
        if (number instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }
        if (number instanceof BigInteger bigInteger) {
            return new BigDecimal(bigInteger);
        }
        return BigDecimal.valueOf(number.doubleValue());
    }

    public static boolean isGreaterThan(Object value, double compareTo) {
        if (value == null) {
            return true;
        }
        if (!(value instanceof Number number)) {
            return false;
        }
        return toBigDecimal(number).compareTo(BigDecimal.valueOf(compareTo)) > 0;
    }

    public static boolean isGreaterThanOrEqual(Object value, double compareTo) {
        if (value == null) {
            return true;
        }
        if (!(value instanceof Number number)) {
            return false;
        }
        return toBigDecimal(number).compareTo(BigDecimal.valueOf(compareTo)) >= 0;
    }

    public static boolean isLessThan(Object value, double compareTo) {
        if (value == null) {
            return true;
        }
        if (!(value instanceof Number number)) {
            return false;
        }
        return toBigDecimal(number).compareTo(BigDecimal.valueOf(compareTo)) < 0;
    }

    public static boolean isLessThanOrEqual(Object value, double compareTo) {
        if (value == null) {
            return true;
        }
        if (!(value instanceof Number number)) {
            return false;
        }
        return toBigDecimal(number).compareTo(BigDecimal.valueOf(compareTo)) <= 0;
    }

    public static boolean isMultipleOf(Object value, double base) {
        if (value == null) {
            return true;
        }
        if (!(value instanceof Number number) || base == 0) {
            return false;
        }
        BigDecimal current = toBigDecimal(number);
        BigDecimal divisor = BigDecimal.valueOf(base);
        BigDecimal[] division = current.divideAndRemainder(divisor);
        return division[1].compareTo(BigDecimal.ZERO) == 0;
    }

    public static int countDigits(Object value) {
        if (value == null) {
            return 0;
        }
        String digits = toDigitSource(value).replaceAll("[^0-9]", "");
        return digits.length();
    }

    private static String toDigitSource(Object value) {
        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal.stripTrailingZeros().toPlainString();
        }
        if (value instanceof Number number) {
            return toBigDecimal(number).stripTrailingZeros().toPlainString();
        }
        return String.valueOf(value);
    }

    public static boolean hasMinDigits(Object value, int minDigits) {
        if (value == null) {
            return true;
        }
        return countDigits(value) >= minDigits;
    }

    public static boolean hasMaxDigits(Object value, int maxDigits) {
        if (value == null) {
            return true;
        }
        return countDigits(value) <= maxDigits;
    }

    public static boolean hasDigitsBetween(Object value, int minDigits, int maxDigits) {
        if (value == null) {
            return true;
        }
        int digits = countDigits(value);
        return digits >= minDigits && digits <= maxDigits;
    }
}
