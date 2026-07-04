package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Ipv4;
import dev.benjaminor.validationplus.support.*;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class Ipv4Validator implements ConstraintValidator<Ipv4, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return NetworkValidationUtils.isIpv4(value);
    }
}
