package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Same;
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

    private String field;
    private String otherField;

    @Override
    public void initialize(Same constraintAnnotation) {
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

        if (EmptyUtils.isEmpty(fieldValue) && EmptyUtils.isEmpty(otherValue)) {
            return true;
        }

        if (Objects.equals(fieldValue, otherValue)) {
            return true;
        }

        return CrossFieldValidationUtils.failOnField(context, otherField);
    }
}
