package dev.benjaminor.validationplus.support;

import java.math.BigDecimal;

/**
 * Utilities for {@code @Digits}.
 */
public final class DigitsValidationUtils {

    private DigitsValidationUtils() {
    }

    public static boolean hasDigits(Number value, int integerDigits, int fractionDigits) {
        if (value == null) {
            return true;
        }
        BigDecimal number = NumberUtils.toBigDecimal(value).stripTrailingZeros();
        String plain = number.toPlainString();
        if (plain.startsWith("-")) {
            plain = plain.substring(1);
        }

        String[] parts = plain.split("\\.");
        String integerPart = parts[0];
        String fractionPart = parts.length > 1 ? parts[1] : "";

        return integerPart.length() <= integerDigits && fractionPart.length() <= fractionDigits;
    }
}
