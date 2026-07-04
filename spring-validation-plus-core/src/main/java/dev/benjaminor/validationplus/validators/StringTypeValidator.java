package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.StringType;
import dev.benjaminor.validationplus.support.TypeUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator para {@link StringType}.
 */
public class StringTypeValidator implements ConstraintValidator<StringType, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return TypeUtils.isStringType(value);
    }
}
