package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Different;
import dev.benjaminor.validationplus.support.CrossFieldConstraintSupport;
import dev.benjaminor.validationplus.support.ReflectionUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

/**
 * Validator for {@link Different}.
 */
public class DifferentValidator implements ConstraintValidator<Different, Object> {

    private String referenceField;
    private String otherField;

    @Override
    public void initialize(Different constraintAnnotation) {
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

        if (!Objects.equals(referenceValue, currentValue)) {
            return true;
        }

        return CrossFieldConstraintSupport.reportViolation(context, value, currentField);
    }
}
