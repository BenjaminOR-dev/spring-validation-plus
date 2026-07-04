package dev.benjaminor.validationplus.exception;

import dev.benjaminor.validationplus.support.ValidationMessageUtils;
import org.springframework.validation.FieldError;

import java.util.Locale;
import java.util.Map;

/**
 * Traduce {@link FieldError} de Spring (binding y validación) a mensajes i18n de validation-plus.
 */
public final class FieldErrorMessageResolver {

    private FieldErrorMessageResolver() {
    }

    public static String resolve(FieldError error, Locale locale) {
        if (isTypeMismatch(error)) {
            return ValidationMessageUtils.resolve(
                    resolveTypeMismatchKey(error.getCodes()),
                    locale,
                    Map.of("field", error.getField()));
        }
        return error.getDefaultMessage();
    }

    private static boolean isTypeMismatch(FieldError error) {
        if (error.getCode() != null && error.getCode().startsWith("typeMismatch")) {
            return true;
        }
        String[] codes = error.getCodes();
        if (codes != null) {
            for (String code : codes) {
                if (code != null && code.startsWith("typeMismatch")) {
                    return true;
                }
            }
        }
        String message = error.getDefaultMessage();
        return message != null && message.startsWith("Failed to convert property value");
    }

    private static String resolveTypeMismatchKey(String[] codes) {
        if (codes != null) {
            for (String code : codes) {
                if (code == null) {
                    continue;
                }
                if (code.contains("Integer") || code.contains("Long")
                        || code.contains("Short") || code.contains("Byte")) {
                    return "dev.benjaminor.validationplus.type.integer";
                }
                if (code.contains("Double") || code.contains("Float")
                        || code.contains("BigDecimal")) {
                    return "dev.benjaminor.validationplus.type.decimal";
                }
                if (code.contains("Boolean")) {
                    return "dev.benjaminor.validationplus.type.boolean";
                }
            }
        }
        return "dev.benjaminor.validationplus.type.generic";
    }
}
