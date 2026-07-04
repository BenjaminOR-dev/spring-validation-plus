package dev.benjaminor.validationplus.exception;

import dev.benjaminor.validationplus.support.ValidationMessageUtils;
import org.springframework.validation.FieldError;

import java.util.Locale;
import java.util.Map;

/**
 * Translates Spring {@link FieldError} instances (binding and validation) into validation-plus i18n messages.
 */
public final class FieldErrorMessageResolver {

    private FieldErrorMessageResolver() {
    }

    public static String resolve(FieldError error, Locale locale) {
        if (isTypeMismatch(error)) {
            return ValidationMessageUtils.resolve(
                    TypeMismatchMessageUtils.resolveMessageKeyFromErrorCodes(error.getCode(), error.getCodes()),
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
}
