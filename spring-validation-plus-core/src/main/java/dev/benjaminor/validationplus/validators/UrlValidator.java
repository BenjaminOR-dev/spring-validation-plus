package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Url;
import dev.benjaminor.validationplus.support.PatternValidationUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator para {@link Url}.
 */
public class UrlValidator implements ConstraintValidator<Url, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return PatternValidationUtils.isUrl(value);
    }
}
