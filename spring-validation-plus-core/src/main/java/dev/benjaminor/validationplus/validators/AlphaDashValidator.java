package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.AlphaDash;
import dev.benjaminor.validationplus.support.*;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AlphaDashValidator implements ConstraintValidator<AlphaDash, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return TextValidationUtils.isAlphaDash(value);
    }
}
