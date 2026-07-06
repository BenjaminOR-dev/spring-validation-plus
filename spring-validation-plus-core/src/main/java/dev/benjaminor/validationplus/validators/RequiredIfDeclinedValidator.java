package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.RequiredIfDeclined;
import dev.benjaminor.validationplus.support.AcceptedValidationUtils;
import dev.benjaminor.validationplus.support.CrossFieldConstraintSupport;
import dev.benjaminor.validationplus.support.CrossFieldValidationUtils;
import dev.benjaminor.validationplus.support.ReflectionUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RequiredIfDeclinedValidator implements ConstraintValidator<RequiredIfDeclined, Object> {

    private String field;
    private String requiredField;

    @Override
    public void initialize(RequiredIfDeclined constraintAnnotation) {
        this.field = CrossFieldConstraintSupport.firstNonBlank(
                constraintAnnotation.field(),
                constraintAnnotation.value());
        this.requiredField = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Object target = CrossFieldConstraintSupport.resolveRoot(value, context);
        if (target == null || field.isBlank()) {
            return true;
        }

        String currentField = CrossFieldConstraintSupport.resolveCurrentField(context, requiredField);
        if (currentField.isBlank()) {
            return true;
        }

        Object fieldValue = ReflectionUtils.getFieldValue(target, field);
        if (!AcceptedValidationUtils.isDeclined(fieldValue)) {
            return true;
        }

        if (CrossFieldValidationUtils.isPresent(target, currentField)) {
            return true;
        }

        return CrossFieldConstraintSupport.reportViolation(context, value, currentField);
    }
}
