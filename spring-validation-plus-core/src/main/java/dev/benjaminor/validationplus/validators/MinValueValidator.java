package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.MinValue;
import dev.benjaminor.validationplus.support.NumberUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for {@link MinValue}.
 */
public class MinValueValidator implements ConstraintValidator<MinValue, Number> {

    private double minValue;

    @Override
    public void initialize(MinValue constraintAnnotation) {
        this.minValue = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Number value, ConstraintValidatorContext context) {
        return NumberUtils.isMinValue(value, minValue);
    }
}
