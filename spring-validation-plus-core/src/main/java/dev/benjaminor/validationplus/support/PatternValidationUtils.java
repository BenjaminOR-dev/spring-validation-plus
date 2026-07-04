package dev.benjaminor.validationplus.support;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

/**
 * Utilities for pattern-based validations.
 */
public final class PatternValidationUtils {

    private static final Pattern UUID_PATTERN = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$");

    private PatternValidationUtils() {
    }

    public static boolean matchesRegex(Object value, Pattern pattern) {
        if (value == null) {
            return true;
        }
        if (!(value instanceof CharSequence charSequence)) {
            return false;
        }
        return pattern.matcher(charSequence.toString()).matches();
    }

    public static boolean notMatchesRegex(Object value, Pattern pattern) {
        if (value == null) {
            return true;
        }
        return !matchesRegex(value, pattern);
    }

    public static boolean isUrl(Object value) {
        if (EmptyUtils.isBlank(value)) {
            return true;
        }
        if (!(value instanceof CharSequence charSequence)) {
            return false;
        }
        String candidate = charSequence.toString().trim();
        try {
            URI uri = new URI(candidate);
            return uri.getScheme() != null && !uri.getScheme().isBlank();
        } catch (URISyntaxException exception) {
            return false;
        }
    }

    public static boolean isUuid(Object value) {
        if (EmptyUtils.isBlank(value)) {
            return true;
        }
        if (!(value instanceof CharSequence charSequence)) {
            return false;
        }
        return UUID_PATTERN.matcher(charSequence.toString().trim()).matches();
    }

    public static boolean startsWith(Object value, String prefix) {
        if (value == null) {
            return true;
        }
        if (!(value instanceof CharSequence charSequence) || prefix == null) {
            return false;
        }
        return charSequence.toString().startsWith(prefix);
    }

    public static boolean endsWith(Object value, String suffix) {
        if (value == null) {
            return true;
        }
        if (!(value instanceof CharSequence charSequence) || suffix == null) {
            return false;
        }
        return charSequence.toString().endsWith(suffix);
    }

    public static boolean notStartsWith(Object value, String prefix) {
        if (value == null) {
            return true;
        }
        return !startsWith(value, prefix);
    }

    public static boolean notEndsWith(Object value, String suffix) {
        if (value == null) {
            return true;
        }
        return !endsWith(value, suffix);
    }
}
