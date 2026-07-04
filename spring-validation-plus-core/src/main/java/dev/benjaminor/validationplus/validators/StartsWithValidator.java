package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.StartsWith;
import dev.benjaminor.validationplus.support.PatternValidationUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for {@link StartsWith}.
 */
public class StartsWithValidator implements ConstraintValidator<StartsWith, Object> {

    private String prefix;

    @Override
    public void initialize(StartsWith constraintAnnotation) {
        this.prefix = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return PatternValidationUtils.startsWith(value, prefix);
    }
}
