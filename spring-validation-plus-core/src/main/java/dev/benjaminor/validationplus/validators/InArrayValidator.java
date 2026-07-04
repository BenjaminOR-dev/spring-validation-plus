package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.InArray;
import dev.benjaminor.validationplus.support.CrossFieldValidationUtils;
import dev.benjaminor.validationplus.support.ReflectionUtils;
import dev.benjaminor.validationplus.support.ArrayValidationUtils;
import dev.benjaminor.validationplus.support.EmptyUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class InArrayValidator implements ConstraintValidator<InArray, Object> {
    private String field;
    private String arrayField;
    @Override
    public void initialize(InArray a) { this.field = a.field(); this.arrayField = a.arrayField(); }
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;
        Object fv = ReflectionUtils.getFieldValue(value, field);
        Object av = ReflectionUtils.getFieldValue(value, arrayField);
        if (EmptyUtils.isEmpty(fv)) return true;
        if (ArrayValidationUtils.isInArray(fv, av)) return true;
        return CrossFieldValidationUtils.failOnField(context, field);
    }
}
