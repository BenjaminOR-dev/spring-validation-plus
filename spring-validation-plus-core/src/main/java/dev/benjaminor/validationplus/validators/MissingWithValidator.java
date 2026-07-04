package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.MissingWith;
import dev.benjaminor.validationplus.support.CrossFieldValidationUtils;
import dev.benjaminor.validationplus.support.ReflectionUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MissingWithValidator implements ConstraintValidator<MissingWith, Object> {
    private String[] fields;
    private String missingField;
    @Override
    public void initialize(MissingWith a) { this.fields = a.fields(); this.missingField = a.missing(); }
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;
        if (!CrossFieldValidationUtils.isAnyPresent(value, fields)) return true;
        if (CrossFieldValidationUtils.isMissing(value, missingField)) return true;
        return CrossFieldValidationUtils.failOnField(context, missingField);
    }
}
