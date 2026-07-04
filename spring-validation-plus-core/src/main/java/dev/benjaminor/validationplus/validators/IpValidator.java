package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Ip;
import dev.benjaminor.validationplus.support.*;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IpValidator implements ConstraintValidator<Ip, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return NetworkValidationUtils.isIp(value);
    }
}
