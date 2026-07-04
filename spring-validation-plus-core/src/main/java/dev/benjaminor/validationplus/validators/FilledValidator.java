package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Filled;
import dev.benjaminor.validationplus.support.*;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FilledValidator implements ConstraintValidator<Filled, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return value == null || !EmptyUtils.isEmpty(value);
    }
}
