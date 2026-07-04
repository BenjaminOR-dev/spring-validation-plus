package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.MaxLength;
import dev.benjaminor.validationplus.support.LengthUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator para {@link MaxLength}.
 */
public class MaxLengthValidator implements ConstraintValidator<MaxLength, Object> {

    private int maxLength;

    @Override
    public void initialize(MaxLength constraintAnnotation) {
        this.maxLength = constraintAnnotation.value();
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
        return length <= maxLength;
    }
}
