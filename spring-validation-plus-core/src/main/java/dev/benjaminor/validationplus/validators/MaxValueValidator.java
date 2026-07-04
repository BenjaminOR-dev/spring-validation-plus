package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.MaxValue;
import dev.benjaminor.validationplus.support.NumberUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for {@link MaxValue}.
 */
public class MaxValueValidator implements ConstraintValidator<MaxValue, Number> {

    private double maxValue;

    @Override
    public void initialize(MaxValue constraintAnnotation) {
        this.maxValue = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Number value, ConstraintValidatorContext context) {
        return NumberUtils.isMaxValue(value, maxValue);
    }
}
