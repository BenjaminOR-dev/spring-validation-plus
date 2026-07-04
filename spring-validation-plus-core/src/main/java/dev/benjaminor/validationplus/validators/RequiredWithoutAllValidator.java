package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.RequiredWithoutAll;
import dev.benjaminor.validationplus.support.CrossFieldValidationUtils;
import dev.benjaminor.validationplus.support.ReflectionUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RequiredWithoutAllValidator implements ConstraintValidator<RequiredWithoutAll, Object> {
    private String[] fields;
    private String requiredField;
    @Override
    public void initialize(RequiredWithoutAll a) { this.fields = a.fields(); this.requiredField = a.required(); }
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;
        if (!CrossFieldValidationUtils.isAllAbsent(value, fields)) return true;
        if (CrossFieldValidationUtils.isPresent(value, requiredField)) return true;
        return CrossFieldValidationUtils.failOnField(context, requiredField);
    }
}
