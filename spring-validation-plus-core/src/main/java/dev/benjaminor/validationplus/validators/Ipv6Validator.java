package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Ipv6;
import dev.benjaminor.validationplus.support.*;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class Ipv6Validator implements ConstraintValidator<Ipv6, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return NetworkValidationUtils.isIpv6(value);
    }
}
