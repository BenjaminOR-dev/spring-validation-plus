package dev.benjaminor.validationplus.support;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Utilidades para validaciones numéricas.
 */
public final class NumberUtils {

    private NumberUtils() {
    }

    /**
     * Indica si el valor representa un entero de Java (no texto ni decimal).
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
     * Indica si el valor es un {@link Number} de Java.
     */
    public static boolean isNumeric(Object value) {
        if (value == null) {
            return true;
        }
        return value instanceof Number;
    }

    /**
     * Indica si el valor es un decimal de Java ({@link Float}, {@link Double} o {@link BigDecimal}).
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
     * Indica si el valor numérico es mayor o igual al mínimo permitido.
     */
    public static boolean isMinValue(Number value, Number min) {
        if (value == null) {
            return true;
        }
        return toBigDecimal(value).compareTo(toBigDecimal(min)) >= 0;
    }

    /**
     * Indica si el valor numérico es menor o igual al máximo permitido.
     */
    public static boolean isMaxValue(Number value, Number max) {
        if (value == null) {
            return true;
        }
        return toBigDecimal(value).compareTo(toBigDecimal(max)) <= 0;
    }

    /**
     * Convierte un {@link Number} a {@link BigDecimal} para comparaciones seguras.
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
        String digits = String.valueOf(value).replaceAll("[^0-9]", "");
        return digits.length();
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
