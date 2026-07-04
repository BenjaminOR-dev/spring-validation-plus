package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Alpha;
import dev.benjaminor.validationplus.support.*;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AlphaValidator implements ConstraintValidator<Alpha, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return TextValidationUtils.isAlpha(value);
    }
}
