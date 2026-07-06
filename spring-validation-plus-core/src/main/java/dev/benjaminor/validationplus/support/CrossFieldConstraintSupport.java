package dev.benjaminor.validationplus.support;

import jakarta.validation.ConstraintValidatorContext;

/**
 * Shared helpers for class-level and field-level cross-field constraints.
 */
public final class CrossFieldConstraintSupport {

    private CrossFieldConstraintSupport() {
    }

    public static Object resolveRoot(Object value, ConstraintValidatorContext context) {
        if (isClassLevelValidation(value, context)) {
            return value;
        }
        Object root = ConstraintContextUtils.getRootBean(context);
        return root != null ? root : value;
    }

    public static boolean isClassLevelValidation(Object value, ConstraintValidatorContext context) {
        String propertyName = ConstraintContextUtils.getPropertyName(context);
        return propertyName == null || propertyName.isBlank();
    }

    public static String resolveCurrentField(ConstraintValidatorContext context, String explicitField) {
        if (explicitField != null && !explicitField.isBlank()) {
            return explicitField;
        }
        String propertyName = ConstraintContextUtils.getPropertyName(context);
        return propertyName != null ? propertyName : "";
    }

    public static boolean reportViolation(ConstraintValidatorContext context, Object value, String explicitField) {
        if (isClassLevelValidation(value, context)) {
            return CrossFieldValidationUtils.failOnField(context, explicitField);
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addConstraintViolation();
        return false;
    }

    public static String[] mergeObservedFields(String[] primary, String[] secondary) {
        if (primary != null && primary.length > 0) {
            return primary;
        }
        return secondary != null ? secondary : new String[0];
    }

    public static String firstNonBlank(String... values) {
        if (values == null) {
            return "";
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return "";
    }
}
