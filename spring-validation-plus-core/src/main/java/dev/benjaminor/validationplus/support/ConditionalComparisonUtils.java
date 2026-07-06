package dev.benjaminor.validationplus.support;

import dev.benjaminor.validationplus.constraints.ConditionalOperator;

import java.util.Objects;

/**
 * Shared comparison logic for conditional cross-field validators.
 */
public final class ConditionalComparisonUtils {

    private ConditionalComparisonUtils() {
    }

    public static boolean matches(Object fieldValue, String expectedValue, ConditionalOperator operator) {
        ConditionalOperator effectiveOperator = operator != null ? operator : ConditionalOperator.EQUALS;
        String actual = String.valueOf(fieldValue);

        return switch (effectiveOperator) {
            case EQUALS -> Objects.equals(actual, expectedValue);
            case NOT_EQUALS -> !Objects.equals(actual, expectedValue);
            case IN -> isIn(actual, expectedValue);
        };
    }

    private static boolean isIn(String actual, String expectedValue) {
        if (expectedValue == null || expectedValue.isBlank()) {
            return false;
        }
        for (String part : expectedValue.split(",")) {
            if (Objects.equals(actual, part.trim())) {
                return true;
            }
        }
        return false;
    }
}
