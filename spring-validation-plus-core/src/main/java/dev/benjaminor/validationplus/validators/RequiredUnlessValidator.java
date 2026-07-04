package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.RequiredUnless;
import dev.benjaminor.validationplus.support.CrossFieldValidationUtils;
import dev.benjaminor.validationplus.support.EmptyUtils;
import dev.benjaminor.validationplus.support.ReflectionUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

/**
 * Validator for {@link RequiredUnless}.
 */
public class RequiredUnlessValidator implements ConstraintValidator<RequiredUnless, Object> {

    private String field;
    private String unlessValue;
    private String requiredField;

    @Override
    public void initialize(RequiredUnless constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.unlessValue = constraintAnnotation.value();
        this.requiredField = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        Object fieldValue = ReflectionUtils.getFieldValue(value, field);
        if (Objects.equals(String.valueOf(fieldValue), unlessValue)) {
            return true;
        }

        Object requiredValue = ReflectionUtils.getFieldValue(value, requiredField);
        if (!EmptyUtils.isEmpty(requiredValue)) {
            return true;
        }

        return CrossFieldValidationUtils.failOnField(context, requiredField);
    }
}
