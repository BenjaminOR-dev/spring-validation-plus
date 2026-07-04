package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.RequiredWithAll;
import dev.benjaminor.validationplus.support.CrossFieldValidationUtils;
import dev.benjaminor.validationplus.support.ReflectionUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RequiredWithAllValidator implements ConstraintValidator<RequiredWithAll, Object> {
    private String[] fields;
    private String requiredField;
    @Override
    public void initialize(RequiredWithAll a) { this.fields = a.fields(); this.requiredField = a.required(); }
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;
        if (!CrossFieldValidationUtils.isAllPresent(value, fields)) return true;
        if (CrossFieldValidationUtils.isPresent(value, requiredField)) return true;
        return CrossFieldValidationUtils.failOnField(context, requiredField);
    }
}
