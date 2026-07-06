package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.ConditionalOperator;
import dev.benjaminor.validationplus.constraints.RequiredUnless;
import dev.benjaminor.validationplus.support.ConditionalComparisonUtils;
import dev.benjaminor.validationplus.support.CrossFieldConstraintSupport;
import dev.benjaminor.validationplus.support.EmptyUtils;
import dev.benjaminor.validationplus.support.ReflectionUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for {@link RequiredUnless}.
 */
public class RequiredUnlessValidator implements ConstraintValidator<RequiredUnless, Object> {

    private String field;
    private String unlessValue;
    private String requiredField;
    private ConditionalOperator operator;

    @Override
    public void initialize(RequiredUnless constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.unlessValue = constraintAnnotation.value();
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
        if (ConditionalComparisonUtils.matches(fieldValue, unlessValue, operator)) {
            return true;
        }

        Object requiredValue = ReflectionUtils.getFieldValue(target, currentField);
        if (!EmptyUtils.isEmpty(requiredValue)) {
            return true;
        }

        return CrossFieldConstraintSupport.reportViolation(context, value, currentField);
    }
}
