package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.MinDigits;
import dev.benjaminor.validationplus.support.NumberUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MinDigitsValidator implements ConstraintValidator<MinDigits, Object> {

    private int minDigits;

    @Override
    public void initialize(MinDigits constraintAnnotation) {
        this.minDigits = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return NumberUtils.hasMinDigits(value, minDigits);
    }
}
