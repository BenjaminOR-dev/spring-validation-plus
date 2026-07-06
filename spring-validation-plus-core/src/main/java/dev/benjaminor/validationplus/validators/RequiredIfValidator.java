package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.ConditionalOperator;
import dev.benjaminor.validationplus.constraints.RequiredIf;
import dev.benjaminor.validationplus.support.ConditionalComparisonUtils;
import dev.benjaminor.validationplus.support.CrossFieldConstraintSupport;
import dev.benjaminor.validationplus.support.EmptyUtils;
import dev.benjaminor.validationplus.support.ReflectionUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for {@link RequiredIf}.
 */
public class RequiredIfValidator implements ConstraintValidator<RequiredIf, Object> {

    private String field;
    private String expectedValue;
    private String requiredField;
    private ConditionalOperator operator;

    @Override
    public void initialize(RequiredIf constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.expectedValue = constraintAnnotation.value();
        this.requiredField = constraintAnnotation.required();
        this.operator = constraintAnnotation.operator();
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

        Object fieldValue = ReflectionUtils.getFieldValue(target, field);
        if (!ConditionalComparisonUtils.matches(fieldValue, expectedValue, operator)) {
            return true;
        }

        Object requiredValue = ReflectionUtils.getFieldValue(target, currentField);
        if (!EmptyUtils.isEmpty(requiredValue)) {
            return true;
        }

        return CrossFieldConstraintSupport.reportViolation(context, value, currentField);
    }
}
