package dev.benjaminor.validationplus.support;

import dev.benjaminor.validationplus.spi.ValidationPlusCheckers;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Optional;

/**
 * Shared utilities for database validations via SPI.
 */
public final class DatabaseValidationUtils {

    private DatabaseValidationUtils() {
    }

    public static Object resolveExcludeId(Object dto, String excludeField, String excludeParameter) {
        if (excludeField != null && !excludeField.isBlank()) {
            Object valueFromDto = ReflectionUtils.getFieldValue(dto, excludeField);
            if (valueFromDto != null) {
                return valueFromDto;
            }
        }
        return resolveContextValue(excludeParameter).orElse(null);
    }

    public static Optional<Object> resolveContextValue(String key) {
        if (key == null || key.isBlank()) {
            return Optional.empty();
        }
        return ValidationPlusCheckers.contextValueProvider()
                .flatMap(provider -> provider.get(key));
    }

    public static boolean failCheckerMissing(
            ConstraintValidatorContext context,
            String field,
            String messageKey) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("{" + messageKey + "}")
                .addPropertyNode(field)
                .addConstraintViolation();
        return false;
    }

    /**
     * Clears registered checkers. Delegation hook for tests.
     */
    public static void resetCheckers() {
        ValidationPlusCheckers.reset();
    }
}
