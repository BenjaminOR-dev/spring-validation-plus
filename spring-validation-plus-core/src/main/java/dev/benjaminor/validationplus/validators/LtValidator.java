package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Lt;
import dev.benjaminor.validationplus.support.NumberUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LtValidator implements ConstraintValidator<Lt, Object> {

    private double compareTo;

    @Override
    public void initialize(Lt constraintAnnotation) {
        this.compareTo = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return NumberUtils.isLessThan(value, compareTo);
    }
}
