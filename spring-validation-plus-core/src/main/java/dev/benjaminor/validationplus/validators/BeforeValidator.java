package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Before;
import dev.benjaminor.validationplus.support.ConstraintContextUtils;
import dev.benjaminor.validationplus.support.DateValidationUtils;
import dev.benjaminor.validationplus.support.EmptyUtils;
import dev.benjaminor.validationplus.support.ReflectionUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for {@link Before}.
 */
public class BeforeValidator implements ConstraintValidator<Before, Object> {

    private String compareToValue;
    private String field;
    private String format;

    @Override
    public void initialize(Before constraintAnnotation) {
        this.compareToValue = constraintAnnotation.value();
        this.field = constraintAnnotation.field();
        this.format = constraintAnnotation.format();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return DateValidationUtils.isBefore(value, resolveCompareTo(context), format);
    }

    private Object resolveCompareTo(ConstraintValidatorContext context) {
        if (!EmptyUtils.isBlank(field)) {
            Object rootBean = ConstraintContextUtils.getRootBean(context);
            if (rootBean != null) {
                return ReflectionUtils.getFieldValue(rootBean, field);
            }
            return null;
        }
        return compareToValue;
    }
}
