package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.ConditionalOperator;
import dev.benjaminor.validationplus.constraints.MissingIf;
import dev.benjaminor.validationplus.support.ConditionalComparisonUtils;
import dev.benjaminor.validationplus.support.CrossFieldConstraintSupport;
import dev.benjaminor.validationplus.support.CrossFieldValidationUtils;
import dev.benjaminor.validationplus.support.ReflectionUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MissingIfValidator implements ConstraintValidator<MissingIf, Object> {

    private String field;
    private String expectedValue;
    private String missingField;
    private ConditionalOperator operator;

    @Override
    public void initialize(MissingIf constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.expectedValue = constraintAnnotation.value();
        this.missingField = constraintAnnotation.missing();
        this.operator = constraintAnnotation.operator();
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

        Object fieldValue = ReflectionUtils.getFieldValue(target, field);
        if (!ConditionalComparisonUtils.matches(fieldValue, expectedValue, operator)) {
            return true;
        }

        if (CrossFieldValidationUtils.isMissing(target, currentField)) {
            return true;
        }

        return CrossFieldConstraintSupport.reportViolation(context, value, currentField);
    }
}
