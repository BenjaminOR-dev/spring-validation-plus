package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Required;
import dev.benjaminor.validationplus.support.EmptyUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for {@link Required}.
 */
public class RequiredValidator implements ConstraintValidator<Required, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return !EmptyUtils.isEmpty(value);
    }
}
