package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.MaxDigits;
import dev.benjaminor.validationplus.support.NumberUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MaxDigitsValidator implements ConstraintValidator<MaxDigits, Object> {

    private int maxDigits;

    @Override
    public void initialize(MaxDigits constraintAnnotation) {
        this.maxDigits = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return NumberUtils.hasMaxDigits(value, maxDigits);
    }
}
