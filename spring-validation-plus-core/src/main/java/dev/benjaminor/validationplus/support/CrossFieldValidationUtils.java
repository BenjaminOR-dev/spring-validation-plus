package dev.benjaminor.validationplus.support;

import jakarta.validation.ConstraintValidatorContext;

/**
 * Utilidades compartidas para validaciones cruzadas a nivel clase.
 */
public final class CrossFieldValidationUtils {

    private CrossFieldValidationUtils() {
    }

    public static boolean failOnField(ConstraintValidatorContext context, String fieldName) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode(fieldName)
                .addConstraintViolation();
        return false;
    }

    public static boolean isPresent(Object target, String fieldName) {
        return !EmptyUtils.isEmpty(ReflectionUtils.getFieldValue(target, fieldName));
    }

    public static boolean isAnyPresent(Object target, String... fieldNames) {
        for (String fieldName : fieldNames) {
            if (isPresent(target, fieldName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAnyAbsent(Object target, String... fieldNames) {
        for (String fieldName : fieldNames) {
            if (!isPresent(target, fieldName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAllPresent(Object target, String... fieldNames) {
        for (String fieldName : fieldNames) {
            if (!isPresent(target, fieldName)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAllAbsent(Object target, String... fieldNames) {
        for (String fieldName : fieldNames) {
            if (isPresent(target, fieldName)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isMissing(Object target, String fieldName) {
        return !isPresent(target, fieldName);
    }

    public static String resolveConfirmationField(String field, String confirmation) {
        if (confirmation != null && !confirmation.isBlank()) {
            return confirmation;
        }
        return field + "Confirmation";
    }
}
