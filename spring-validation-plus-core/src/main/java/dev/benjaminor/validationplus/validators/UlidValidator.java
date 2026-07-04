package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Ulid;
import dev.benjaminor.validationplus.support.*;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UlidValidator implements ConstraintValidator<Ulid, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return TextValidationUtils.isUlid(value);
    }
}
