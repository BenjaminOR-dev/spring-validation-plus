package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Gte;
import dev.benjaminor.validationplus.support.NumberUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class GteValidator implements ConstraintValidator<Gte, Object> {

    private double compareTo;

    @Override
    public void initialize(Gte constraintAnnotation) {
        this.compareTo = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return NumberUtils.isGreaterThanOrEqual(value, compareTo);
    }
}
