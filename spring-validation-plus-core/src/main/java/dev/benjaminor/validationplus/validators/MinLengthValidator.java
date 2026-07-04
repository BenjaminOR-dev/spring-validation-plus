package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.MinLength;
import dev.benjaminor.validationplus.support.LengthUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator para {@link MinLength}.
 */
public class MinLengthValidator implements ConstraintValidator<MinLength, Object> {

    private int minLength;

    @Override
    public void initialize(MinLength constraintAnnotation) {
        this.minLength = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        int length = LengthUtils.getLength(value);
        if (length < 0) {
            return false;
        }
        return length >= minLength;
    }
}
