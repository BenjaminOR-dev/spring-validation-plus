package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.IntegerType;
import dev.benjaminor.validationplus.support.NumberUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator para {@link IntegerType}.
 */
public class IntegerTypeValidator implements ConstraintValidator<IntegerType, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return NumberUtils.isIntegerType(value);
    }
}
