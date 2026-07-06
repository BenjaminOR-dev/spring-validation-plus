package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.RequiredWithoutAll;
import dev.benjaminor.validationplus.support.CrossFieldConstraintSupport;
import dev.benjaminor.validationplus.support.CrossFieldValidationUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RequiredWithoutAllValidator implements ConstraintValidator<RequiredWithoutAll, Object> {

    private String[] fields;
    private String requiredField;

    @Override
    public void initialize(RequiredWithoutAll constraintAnnotation) {
        this.fields = CrossFieldConstraintSupport.mergeObservedFields(
                constraintAnnotation.value(),
                constraintAnnotation.fields());
        this.requiredField = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Object target = CrossFieldConstraintSupport.resolveRoot(value, context);
        if (target == null) {
            return true;
        }

        String currentField = CrossFieldConstraintSupport.resolveCurrentField(context, requiredField);
        if (currentField.isBlank()) {
            return true;
        }

        if (!CrossFieldValidationUtils.isAllAbsent(target, fields)) {
            return true;
        }

        if (CrossFieldValidationUtils.isPresent(target, currentField)) {
            return true;
        }

        return CrossFieldConstraintSupport.reportViolation(context, value, currentField);
    }
}
