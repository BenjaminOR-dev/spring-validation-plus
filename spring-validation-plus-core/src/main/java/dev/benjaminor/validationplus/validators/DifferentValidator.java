package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Different;
import dev.benjaminor.validationplus.support.CrossFieldValidationUtils;
import dev.benjaminor.validationplus.support.ReflectionUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

/**
 * Validator para {@link Different}.
 */
public class DifferentValidator implements ConstraintValidator<Different, Object> {

    private String field;
    private String otherField;

    @Override
    public void initialize(Different constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.otherField = constraintAnnotation.other();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        Object fieldValue = ReflectionUtils.getFieldValue(value, field);
        Object otherValue = ReflectionUtils.getFieldValue(value, otherField);

        if (!Objects.equals(fieldValue, otherValue)) {
            return true;
        }

        return CrossFieldValidationUtils.failOnField(context, otherField);
    }
}
