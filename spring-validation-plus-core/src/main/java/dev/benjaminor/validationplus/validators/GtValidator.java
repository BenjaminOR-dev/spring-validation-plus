package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Gt;
import dev.benjaminor.validationplus.support.NumberUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class GtValidator implements ConstraintValidator<Gt, Object> {

    private double compareTo;

    @Override
    public void initialize(Gt constraintAnnotation) {
        this.compareTo = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return NumberUtils.isGreaterThan(value, compareTo);
    }
}
