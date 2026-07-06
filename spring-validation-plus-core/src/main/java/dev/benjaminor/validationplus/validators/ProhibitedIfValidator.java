package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.ConditionalOperator;
import dev.benjaminor.validationplus.constraints.ProhibitedIf;
import dev.benjaminor.validationplus.support.ConditionalComparisonUtils;
import dev.benjaminor.validationplus.support.CrossFieldConstraintSupport;
import dev.benjaminor.validationplus.support.CrossFieldValidationUtils;
import dev.benjaminor.validationplus.support.ReflectionUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ProhibitedIfValidator implements ConstraintValidator<ProhibitedIf, Object> {

    private String field;
    private String expectedValue;
    private String prohibitedField;
    private ConditionalOperator operator;

    @Override
    public void initialize(ProhibitedIf constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.expectedValue = constraintAnnotation.value();
        this.prohibitedField = constraintAnnotation.prohibited();
        this.operator = constraintAnnotation.operator();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Object target = CrossFieldConstraintSupport.resolveRoot(value, context);
        if (target == null) {
            return true;
        }

        String currentField = CrossFieldConstraintSupport.resolveCurrentField(context, prohibitedField);
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
