package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Distinct;
import dev.benjaminor.validationplus.support.*;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DistinctValidator implements ConstraintValidator<Distinct, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return ArrayValidationUtils.isDistinct(value);
    }
}
