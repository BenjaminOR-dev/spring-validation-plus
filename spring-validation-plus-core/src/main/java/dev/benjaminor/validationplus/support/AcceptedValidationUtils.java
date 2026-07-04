package dev.benjaminor.validationplus.support;

import java.util.Locale;
import java.util.Set;

/**
 * Utilities for {@code @Accepted} and {@code @Declined}.
 */
public final class AcceptedValidationUtils {

    private static final Set<String> ACCEPTED = Set.of("yes", "on", "1", "true", "t");
    private static final Set<String> DECLINED = Set.of("no", "off", "0", "false", "f");

    private AcceptedValidationUtils() {
    }

    public static boolean isAccepted(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean booleanValue) {
            return booleanValue;
        }
        return ACCEPTED.contains(String.valueOf(value).trim().toLowerCase(Locale.ROOT));
    }

    public static boolean isDeclined(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean booleanValue) {
            return !booleanValue;
        }
        return DECLINED.contains(String.valueOf(value).trim().toLowerCase(Locale.ROOT));
    }
}
