package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Numeric;
import dev.benjaminor.validationplus.support.NumberUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator para {@link Numeric}.
 */
public class NumericValidator implements ConstraintValidator<Numeric, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return NumberUtils.isNumeric(value);
    }
}
