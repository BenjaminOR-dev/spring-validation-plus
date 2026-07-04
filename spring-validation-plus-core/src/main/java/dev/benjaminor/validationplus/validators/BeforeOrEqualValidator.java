package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.BeforeOrEqual;
import dev.benjaminor.validationplus.support.ConstraintContextUtils;
import dev.benjaminor.validationplus.support.DateValidationUtils;
import dev.benjaminor.validationplus.support.EmptyUtils;
import dev.benjaminor.validationplus.support.ReflectionUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BeforeOrEqualValidator implements ConstraintValidator<BeforeOrEqual, Object> {

    private String compareToValue;
    private String field;
    private String format;

    @Override
    public void initialize(BeforeOrEqual constraintAnnotation) {
        this.compareToValue = constraintAnnotation.value();
        this.field = constraintAnnotation.field();
        this.format = constraintAnnotation.format();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return DateValidationUtils.isBeforeOrEqual(value, resolveCompareTo(context), format);
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
