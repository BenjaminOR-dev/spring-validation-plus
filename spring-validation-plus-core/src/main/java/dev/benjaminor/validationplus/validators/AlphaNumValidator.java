package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.AlphaNum;
import dev.benjaminor.validationplus.support.*;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AlphaNumValidator implements ConstraintValidator<AlphaNum, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return TextValidationUtils.isAlphaNum(value);
    }
}
