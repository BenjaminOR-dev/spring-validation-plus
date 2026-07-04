package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Prohibited;
import dev.benjaminor.validationplus.support.CrossFieldValidationUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ProhibitedValidator implements ConstraintValidator<Prohibited, Object> {

    private String prohibitedField;

    @Override
    public void initialize(Prohibited constraintAnnotation) {
        this.prohibitedField = constraintAnnotation.field();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (CrossFieldValidationUtils.isMissing(value, prohibitedField)) {
            return true;
        }
        return CrossFieldValidationUtils.failOnField(context, prohibitedField);
    }
}
