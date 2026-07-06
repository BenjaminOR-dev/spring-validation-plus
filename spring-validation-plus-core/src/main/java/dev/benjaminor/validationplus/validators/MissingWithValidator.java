package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.MissingWith;
import dev.benjaminor.validationplus.support.CrossFieldConstraintSupport;
import dev.benjaminor.validationplus.support.CrossFieldValidationUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MissingWithValidator implements ConstraintValidator<MissingWith, Object> {

    private String[] fields;
    private String missingField;

    @Override
    public void initialize(MissingWith constraintAnnotation) {
        this.fields = CrossFieldConstraintSupport.mergeObservedFields(
                constraintAnnotation.value(),
                constraintAnnotation.fields());
        this.missingField = constraintAnnotation.missing();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Object target = CrossFieldConstraintSupport.resolveRoot(value, context);
        if (target == null) {
            return true;
        }

        String currentField = CrossFieldConstraintSupport.resolveCurrentField(context, missingField);
        if (currentField.isBlank()) {
            return true;
        }

        if (!CrossFieldValidationUtils.isAnyPresent(target, fields)) {
            return true;
        }

        if (CrossFieldValidationUtils.isMissing(target, currentField)) {
            return true;
        }

        return CrossFieldConstraintSupport.reportViolation(context, value, currentField);
    }
}
