package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.ArrayType;
import dev.benjaminor.validationplus.support.TypeUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for {@link ArrayType}.
 */
public class ArrayTypeValidator implements ConstraintValidator<ArrayType, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return TypeUtils.isArrayType(value);
    }
}
