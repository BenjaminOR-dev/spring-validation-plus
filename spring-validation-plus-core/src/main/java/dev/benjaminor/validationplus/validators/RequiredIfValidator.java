package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.RequiredIf;
import dev.benjaminor.validationplus.support.CrossFieldValidationUtils;
import dev.benjaminor.validationplus.support.EmptyUtils;
import dev.benjaminor.validationplus.support.ReflectionUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

/**
 * Validator for {@link RequiredIf}.
 */
public class RequiredIfValidator implements ConstraintValidator<RequiredIf, Object> {

    private String field;
    private String expectedValue;
    private String requiredField;

    @Override
    public void initialize(RequiredIf constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.expectedValue = constraintAnnotation.value();
        this.requiredField = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        Object fieldValue = ReflectionUtils.getFieldValue(value, field);
        if (!Objects.equals(String.valueOf(fieldValue), expectedValue)) {
            return true;
        }

        Object requiredValue = ReflectionUtils.getFieldValue(value, requiredField);
        if (!EmptyUtils.isEmpty(requiredValue)) {
            return true;
        }

        return CrossFieldValidationUtils.failOnField(context, requiredField);
    }
}
