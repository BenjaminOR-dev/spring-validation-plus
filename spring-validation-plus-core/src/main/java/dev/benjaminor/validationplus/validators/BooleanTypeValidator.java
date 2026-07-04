package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.BooleanType;
import dev.benjaminor.validationplus.support.TypeUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator para {@link BooleanType}.
 */
public class BooleanTypeValidator implements ConstraintValidator<BooleanType, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return TypeUtils.isBooleanType(value);
    }
}
