package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Same;
import dev.benjaminor.validationplus.support.CrossFieldConstraintSupport;
import dev.benjaminor.validationplus.support.CrossFieldValidationUtils;
import dev.benjaminor.validationplus.support.EmptyUtils;
import dev.benjaminor.validationplus.support.ReflectionUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

/**
 * Validator for {@link Same}.
 */
public class SameValidator implements ConstraintValidator<Same, Object> {

    private String referenceField;
    private String otherField;

    @Override
    public void initialize(Same constraintAnnotation) {
        this.referenceField = CrossFieldConstraintSupport.firstNonBlank(
                constraintAnnotation.field(),
                constraintAnnotation.value());
        this.otherField = constraintAnnotation.other();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Object target = CrossFieldConstraintSupport.resolveRoot(value, context);
        if (target == null || referenceField.isBlank()) {
            return true;
        }

        String currentField = CrossFieldConstraintSupport.resolveCurrentField(context, otherField);
        if (currentField.isBlank()) {
            return true;
        }

        Object referenceValue = ReflectionUtils.getFieldValue(target, referenceField);
        Object currentValue = ReflectionUtils.getFieldValue(target, currentField);

        if (EmptyUtils.isEmpty(currentValue) || EmptyUtils.isEmpty(referenceValue)) {
            return true;
        }

        if (Objects.equals(referenceValue, currentValue)) {
            return true;
        }

        return CrossFieldConstraintSupport.reportViolation(context, value, currentField);
    }
}
