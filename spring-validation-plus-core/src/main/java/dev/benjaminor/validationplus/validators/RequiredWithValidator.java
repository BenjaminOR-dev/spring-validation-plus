package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.RequiredWith;
import dev.benjaminor.validationplus.support.CrossFieldValidationUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for {@link RequiredWith}.
 */
public class RequiredWithValidator implements ConstraintValidator<RequiredWith, Object> {

    private String[] fields;
    private String requiredField;

    @Override
    public void initialize(RequiredWith constraintAnnotation) {
        this.fields = constraintAnnotation.fields();
        this.requiredField = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (!CrossFieldValidationUtils.isAnyPresent(value, fields)) {
            return true;
        }

        if (CrossFieldValidationUtils.isPresent(value, requiredField)) {
            return true;
        }

        return CrossFieldValidationUtils.failOnField(context, requiredField);
    }
}
