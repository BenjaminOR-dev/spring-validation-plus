package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Uuid;
import dev.benjaminor.validationplus.support.PatternValidationUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for {@link Uuid}.
 */
public class UuidValidator implements ConstraintValidator<Uuid, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return PatternValidationUtils.isUuid(value);
    }
}
