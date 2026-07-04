package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.MultipleOf;
import dev.benjaminor.validationplus.support.NumberUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MultipleOfValidator implements ConstraintValidator<MultipleOf, Object> {

    private double base;

    @Override
    public void initialize(MultipleOf constraintAnnotation) {
        this.base = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return NumberUtils.isMultipleOf(value, base);
    }
}
