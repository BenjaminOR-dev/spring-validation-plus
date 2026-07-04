package dev.benjaminor.validationplus.exception;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;

/**
 * Resolves i18n keys for type conversion errors (binding, JSON, query params).
 */
public final class TypeMismatchMessageUtils {

    public static final String INTEGER = "dev.benjaminor.validationplus.type.integer";
    public static final String DECIMAL = "dev.benjaminor.validationplus.type.decimal";
    public static final String BOOLEAN = "dev.benjaminor.validationplus.type.boolean";
    public static final String STRING = "dev.benjaminor.validationplus.type.string";
    public static final String ARRAY = "dev.benjaminor.validationplus.type.array";
    public static final String GENERIC = "dev.benjaminor.validationplus.type.generic";

    private TypeMismatchMessageUtils() {
    }

    public static String resolveMessageKey(Class<?> targetType) {
        if (targetType == null) {
            return GENERIC;
        }
        if (isIntegerType(targetType)) {
            return INTEGER;
        }
        if (isDecimalType(targetType)) {
            return DECIMAL;
        }
        if (Boolean.class.equals(targetType) || boolean.class.equals(targetType)) {
            return BOOLEAN;
        }
        if (CharSequence.class.isAssignableFrom(targetType)) {
            return STRING;
        }
        if (targetType.isArray() || Collection.class.isAssignableFrom(targetType)) {
            return ARRAY;
        }
        return GENERIC;
    }

    public static String resolveMessageKeyFromErrorCodes(String code, String[] codes) {
        String key = resolveMessageKeyFromCodeString(code);
        if (key != null) {
            return key;
        }
        if (codes != null) {
            for (String candidate : codes) {
                key = resolveMessageKeyFromCodeString(candidate);
                if (key != null) {
                    return key;
                }
            }
        }
        return GENERIC;
    }

    private static String resolveMessageKeyFromCodeString(String code) {
        if (code == null || !code.startsWith("typeMismatch")) {
            return null;
        }
        if (containsAny(code, "Integer", "Long", "Short", "Byte", "BigInteger", ".int", ".long")) {
            return INTEGER;
        }
        if (containsAny(code, "Double", "Float", "BigDecimal", ".double", ".float")) {
            return DECIMAL;
        }
        if (containsAny(code, "Boolean", ".boolean")) {
            return BOOLEAN;
        }
        if (containsAny(code, "String", "CharSequence")) {
            return STRING;
        }
        if (containsAny(code, "[]", "List", "Collection", "Set")) {
            return ARRAY;
        }
        return null;
    }

    private static boolean isIntegerType(Class<?> targetType) {
        return Integer.class.equals(targetType) || int.class.equals(targetType)
                || Long.class.equals(targetType) || long.class.equals(targetType)
                || Short.class.equals(targetType) || short.class.equals(targetType)
                || Byte.class.equals(targetType) || byte.class.equals(targetType)
                || BigInteger.class.equals(targetType);
    }

    private static boolean isDecimalType(Class<?> targetType) {
        return Float.class.equals(targetType) || float.class.equals(targetType)
                || Double.class.equals(targetType) || double.class.equals(targetType)
                || BigDecimal.class.equals(targetType);
    }

    private static boolean containsAny(String value, String... tokens) {
        for (String token : tokens) {
            if (value.contains(token)) {
                return true;
            }
        }
        return false;
    }
}
