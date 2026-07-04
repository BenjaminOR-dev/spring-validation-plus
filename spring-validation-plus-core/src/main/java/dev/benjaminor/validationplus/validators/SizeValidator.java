package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Size;
import dev.benjaminor.validationplus.support.RangeValidationUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator para {@link Size}.
 */
public class SizeValidator implements ConstraintValidator<Size, Object> {

    private int expectedSize;

    @Override
    public void initialize(Size constraintAnnotation) {
        this.expectedSize = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return RangeValidationUtils.hasExactSize(value, expectedSize);
    }
}
