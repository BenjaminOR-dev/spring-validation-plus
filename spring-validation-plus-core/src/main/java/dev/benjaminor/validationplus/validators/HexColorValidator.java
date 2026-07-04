package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.HexColor;
import dev.benjaminor.validationplus.support.*;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class HexColorValidator implements ConstraintValidator<HexColor, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return TextValidationUtils.isHexColor(value);
    }
}
