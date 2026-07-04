package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.MacAddress;
import dev.benjaminor.validationplus.support.*;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MacAddressValidator implements ConstraintValidator<MacAddress, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return NetworkValidationUtils.isMacAddress(value);
    }
}
