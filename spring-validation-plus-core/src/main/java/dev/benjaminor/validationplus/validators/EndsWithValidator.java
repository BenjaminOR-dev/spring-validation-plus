package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.EndsWith;
import dev.benjaminor.validationplus.support.PatternValidationUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator para {@link EndsWith}.
 */
public class EndsWithValidator implements ConstraintValidator<EndsWith, Object> {

    private String suffix;

    @Override
    public void initialize(EndsWith constraintAnnotation) {
        this.suffix = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return PatternValidationUtils.endsWith(value, suffix);
    }
}
