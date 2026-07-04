package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.ProhibitedUnless;
import dev.benjaminor.validationplus.support.CrossFieldValidationUtils;
import dev.benjaminor.validationplus.support.ReflectionUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ProhibitedUnlessValidator implements ConstraintValidator<ProhibitedUnless, Object> {
    private String field;
    private String expectedValue;
    private String prohibitedField;
    @Override
    public void initialize(ProhibitedUnless a) {
        this.field = a.field(); this.expectedValue = a.value(); this.prohibitedField = a.prohibited();
    }
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;
        Object fv = ReflectionUtils.getFieldValue(value, field);
        if (java.util.Objects.equals(String.valueOf(fv), expectedValue)) return true;
        if (CrossFieldValidationUtils.isMissing(value, prohibitedField)) return true;
        return CrossFieldValidationUtils.failOnField(context, prohibitedField);
    }
}
