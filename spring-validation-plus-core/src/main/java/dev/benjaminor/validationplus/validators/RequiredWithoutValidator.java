package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.RequiredWithout;
import dev.benjaminor.validationplus.support.CrossFieldValidationUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator para {@link RequiredWithout}.
 */
public class RequiredWithoutValidator implements ConstraintValidator<RequiredWithout, Object> {

    private String[] fields;
    private String requiredField;

    @Override
    public void initialize(RequiredWithout constraintAnnotation) {
        this.fields = constraintAnnotation.fields();
        this.requiredField = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (!CrossFieldValidationUtils.isAnyAbsent(value, fields)) {
            return true;
        }

        if (CrossFieldValidationUtils.isPresent(value, requiredField)) {
            return true;
        }

        return CrossFieldValidationUtils.failOnField(context, requiredField);
    }
}
