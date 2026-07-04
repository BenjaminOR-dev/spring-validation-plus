package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Timezone;
import dev.benjaminor.validationplus.support.*;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TimezoneValidator implements ConstraintValidator<Timezone, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return DateValidationUtils.isTimezone(value);
    }
}
