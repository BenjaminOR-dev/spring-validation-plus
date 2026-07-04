package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Accepted;
import dev.benjaminor.validationplus.support.AcceptedValidationUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator para {@link Accepted}.
 */
public class AcceptedValidator implements ConstraintValidator<Accepted, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return AcceptedValidationUtils.isAccepted(value);
    }
}
