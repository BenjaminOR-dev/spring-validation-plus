package dev.benjaminor.validationplus.support;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Utilities for validaciones de texto tipo Laravel.
 */
public final class TextValidationUtils {

    private static final Pattern ALPHA = Pattern.compile("^\\p{L}+$");
    private static final Pattern ALPHA_NUM = Pattern.compile("^[\\p{L}\\p{N}]+$");
    private static final Pattern ALPHA_DASH = Pattern.compile("^[\\p{L}\\p{N}_-]+$");
    private static final Pattern ASCII = Pattern.compile("^[\u0000-\u007F]+$");
    private static final Pattern HEX_COLOR = Pattern.compile("^#?([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
    private static final Pattern ULID = Pattern.compile("^[0-7][0-9A-HJKMNP-TV-Z]{25}$", Pattern.CASE_INSENSITIVE);

    private TextValidationUtils() {
    }

    public static boolean isAlpha(Object value) {
        return PatternValidationUtils.matchesRegex(value, ALPHA);
    }

    public static boolean isAlphaNum(Object value) {
        return PatternValidationUtils.matchesRegex(value, ALPHA_NUM);
    }

    public static boolean isAlphaDash(Object value) {
        return PatternValidationUtils.matchesRegex(value, ALPHA_DASH);
    }

    public static boolean isAscii(Object value) {
        return PatternValidationUtils.matchesRegex(value, ASCII);
    }

    public static boolean isLowercase(Object value) {
        if (EmptyUtils.isBlank(value)) {
            return true;
        }
        if (!(value instanceof CharSequence charSequence)) {
            return false;
        }
        String text = charSequence.toString();
        return text.equals(text.toLowerCase(Locale.ROOT));
    }

    public static boolean isUppercase(Object value) {
        if (EmptyUtils.isBlank(value)) {
            return true;
        }
        if (!(value instanceof CharSequence charSequence)) {
            return false;
        }
        String text = charSequence.toString();
        return text.equals(text.toUpperCase(Locale.ROOT));
    }

    public static boolean isHexColor(Object value) {
        return PatternValidationUtils.matchesRegex(value, HEX_COLOR);
    }

    public static boolean isUlid(Object value) {
        return PatternValidationUtils.matchesRegex(value, ULID);
    }
}
