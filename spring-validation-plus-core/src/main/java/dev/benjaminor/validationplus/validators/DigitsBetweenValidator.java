package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.DigitsBetween;
import dev.benjaminor.validationplus.support.NumberUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DigitsBetweenValidator implements ConstraintValidator<DigitsBetween, Object> {
    private int min;
    private int max;
    @Override
    public void initialize(DigitsBetween a) { this.min = a.min(); this.max = a.max(); }
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return NumberUtils.hasDigitsBetween(value, min, max);
    }
}
