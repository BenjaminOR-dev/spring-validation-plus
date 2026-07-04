package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Digits;
import dev.benjaminor.validationplus.support.DigitsValidationUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator para {@link Digits}.
 */
public class DigitsValidator implements ConstraintValidator<Digits, Number> {

    private int integerDigits;
    private int fractionDigits;

    @Override
    public void initialize(Digits constraintAnnotation) {
        this.integerDigits = constraintAnnotation.integer();
        this.fractionDigits = constraintAnnotation.fraction();
    }

    @Override
    public boolean isValid(Number value, ConstraintValidatorContext context) {
        return DigitsValidationUtils.hasDigits(value, integerDigits, fractionDigits);
    }
}
