package dev.benjaminor.validationplus.support;

import java.util.Locale;

/**
 * Utilidades para {@code @Enum}.
 */
public final class EnumValidationUtils {

    private EnumValidationUtils() {
    }

    public static boolean isEnumConstant(Object value, Class<? extends Enum<?>> enumClass, boolean ignoreCase) {
        if (value == null) {
            return true;
        }
        if (enumClass == null) {
            return false;
        }
        if (enumClass.isInstance(value)) {
            return true;
        }
        String candidate = String.valueOf(value).trim();
        for (Enum<?> constant : enumClass.getEnumConstants()) {
            String constantName = constant.name();
            if (ignoreCase) {
                if (constantName.equalsIgnoreCase(candidate)) {
                    return true;
                }
            } else if (constantName.equals(candidate)) {
                return true;
            }
        }
        return false;
    }
}
