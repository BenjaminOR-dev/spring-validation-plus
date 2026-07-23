package dev.benjaminor.validationplus.support;

import jakarta.validation.ConstraintValidatorContext;

/**
 * Shared utilities for class-level cross-field validations.
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

    /**
     * The field has a non-empty value ({@link EmptyUtils#isEmpty}).
     * Used by {@code @RequiredWith}, {@code @RequiredIf}, etc.
     */
    public static boolean hasValue(Object target, String fieldName) {
        return !EmptyUtils.isEmpty(ReflectionUtils.getFieldValue(target, fieldName));
    }

    /**
     * The field was bound in the payload (non-null reference).
     * Used by {@code @Missing} and {@code @Prohibited} rules.
     */
    public static boolean isProvided(Object target, String fieldName) {
        return ReflectionUtils.getFieldValue(target, fieldName) != null;
    }

    public static boolean isPresent(Object target, String fieldName) {
        return hasValue(target, fieldName);
    }

    public static boolean isAnyPresent(Object target, String... fieldNames) {
        for (String fieldName : fieldNames) {
            if (hasValue(target, fieldName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAnyAbsent(Object target, String... fieldNames) {
        for (String fieldName : fieldNames) {
            if (!isProvided(target, fieldName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAllPresent(Object target, String... fieldNames) {
        for (String fieldName : fieldNames) {
            if (!hasValue(target, fieldName)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAllAbsent(Object target, String... fieldNames) {
        for (String fieldName : fieldNames) {
            if (isProvided(target, fieldName)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isMissing(Object target, String fieldName) {
        return !isProvided(target, fieldName);
    }
}
