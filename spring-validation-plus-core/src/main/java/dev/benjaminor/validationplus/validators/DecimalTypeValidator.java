package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.DecimalType;
import dev.benjaminor.validationplus.support.NumberUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator para {@link DecimalType}.
 */
public class DecimalTypeValidator implements ConstraintValidator<DecimalType, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return NumberUtils.isDecimalType(value);
    }
}
