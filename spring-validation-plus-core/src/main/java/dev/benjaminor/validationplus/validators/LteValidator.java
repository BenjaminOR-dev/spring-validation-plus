package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Lte;
import dev.benjaminor.validationplus.support.NumberUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LteValidator implements ConstraintValidator<Lte, Object> {

    private double compareTo;

    @Override
    public void initialize(Lte constraintAnnotation) {
        this.compareTo = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return NumberUtils.isLessThanOrEqual(value, compareTo);
    }
}
