package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Nullable;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for {@link Nullable}. Always valid.
 */
public class NullableValidator implements ConstraintValidator<Nullable, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return true;
    }
}
