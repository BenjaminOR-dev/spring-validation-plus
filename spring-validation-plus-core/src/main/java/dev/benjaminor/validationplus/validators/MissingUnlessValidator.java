package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.MissingUnless;
import dev.benjaminor.validationplus.support.CrossFieldValidationUtils;
import dev.benjaminor.validationplus.support.ReflectionUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MissingUnlessValidator implements ConstraintValidator<MissingUnless, Object> {
    private String field;
    private String expectedValue;
    private String missingField;
    @Override
    public void initialize(MissingUnless a) {
        this.field = a.field(); this.expectedValue = a.value(); this.missingField = a.missing();
    }
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;
        Object fv = ReflectionUtils.getFieldValue(value, field);
        if (java.util.Objects.equals(String.valueOf(fv), expectedValue)) return true;
        if (CrossFieldValidationUtils.isMissing(value, missingField)) return true;
        return CrossFieldValidationUtils.failOnField(context, missingField);
    }
}
