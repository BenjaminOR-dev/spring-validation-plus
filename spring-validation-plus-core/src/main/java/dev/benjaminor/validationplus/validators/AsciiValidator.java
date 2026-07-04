package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Ascii;
import dev.benjaminor.validationplus.support.*;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AsciiValidator implements ConstraintValidator<Ascii, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return TextValidationUtils.isAscii(value);
    }
}
