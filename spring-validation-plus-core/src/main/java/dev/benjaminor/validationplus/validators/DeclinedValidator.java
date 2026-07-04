package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Declined;
import dev.benjaminor.validationplus.support.AcceptedValidationUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator para {@link Declined}.
 */
public class DeclinedValidator implements ConstraintValidator<Declined, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return AcceptedValidationUtils.isDeclined(value);
    }
}
