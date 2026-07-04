package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Missing;
import dev.benjaminor.validationplus.support.CrossFieldValidationUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MissingValidator implements ConstraintValidator<Missing, Object> {

    private String missingField;

    @Override
    public void initialize(Missing constraintAnnotation) {
        this.missingField = constraintAnnotation.field();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (CrossFieldValidationUtils.isMissing(value, missingField)) {
            return true;
        }
        return CrossFieldValidationUtils.failOnField(context, missingField);
    }
}
