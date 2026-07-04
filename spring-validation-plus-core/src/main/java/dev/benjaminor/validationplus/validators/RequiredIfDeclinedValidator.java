package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.RequiredIfDeclined;
import dev.benjaminor.validationplus.support.AcceptedValidationUtils;
import dev.benjaminor.validationplus.support.CrossFieldValidationUtils;
import dev.benjaminor.validationplus.support.ReflectionUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RequiredIfDeclinedValidator implements ConstraintValidator<RequiredIfDeclined, Object> {
    private String field;
    private String requiredField;
    @Override
    public void initialize(RequiredIfDeclined a) { this.field = a.field(); this.requiredField = a.required(); }
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;
        Object fv = ReflectionUtils.getFieldValue(value, field);
        if (!AcceptedValidationUtils.isDeclined(fv)) return true;
        if (CrossFieldValidationUtils.isPresent(value, requiredField)) return true;
        return CrossFieldValidationUtils.failOnField(context, requiredField);
    }
}
