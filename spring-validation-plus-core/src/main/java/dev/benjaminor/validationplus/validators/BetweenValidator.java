package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Between;
import dev.benjaminor.validationplus.support.RangeValidationUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator para {@link Between}.
 */
public class BetweenValidator implements ConstraintValidator<Between, Object> {

    private double min;
    private double max;

    @Override
    public void initialize(Between constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return RangeValidationUtils.isBetween(value, min, max);
    }
}
