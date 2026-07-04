package dev.benjaminor.validationplus.support;

/**
 * Utilidades para {@code @Password}.
 */
public final class PasswordValidationUtils {

    private PasswordValidationUtils() {
    }

    public static boolean isValid(
            Object value,
            int minLength,
            boolean letters,
            boolean mixedCase,
            boolean numbers,
            boolean symbols) {
        if (value == null) {
            return true;
        }
        if (!(value instanceof CharSequence charSequence)) {
            return false;
        }
        String password = charSequence.toString();
        if (password.length() < minLength) {
            return false;
        }
        boolean hasLetter = false;
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasNumber = false;
        boolean hasSymbol = false;

        for (char character : password.toCharArray()) {
            if (Character.isLetter(character)) {
                hasLetter = true;
                if (Character.isUpperCase(character)) {
                    hasUpper = true;
                }
                if (Character.isLowerCase(character)) {
                    hasLower = true;
                }
            } else if (Character.isDigit(character)) {
                hasNumber = true;
            } else {
                hasSymbol = true;
            }
        }

        if (letters && !hasLetter) {
            return false;
        }
        if (mixedCase && !(hasUpper && hasLower)) {
            return false;
        }
        if (numbers && !hasNumber) {
            return false;
        }
        if (symbols && !hasSymbol) {
            return false;
        }
        return true;
    }
}
