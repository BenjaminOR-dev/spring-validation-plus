package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.ActiveUrl;
import dev.benjaminor.validationplus.support.*;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ActiveUrlValidator implements ConstraintValidator<ActiveUrl, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return NetworkValidationUtils.isActiveUrl(value);
    }
}
