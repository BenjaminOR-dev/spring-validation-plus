package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Lowercase;
import dev.benjaminor.validationplus.support.*;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LowercaseValidator implements ConstraintValidator<Lowercase, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return TextValidationUtils.isLowercase(value);
    }
}
