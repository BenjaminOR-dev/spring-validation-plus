package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.NotEndsWith;
import dev.benjaminor.validationplus.support.PatternValidationUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotEndsWithValidator implements ConstraintValidator<NotEndsWith, Object> {
    private String suffix;
    @Override
    public void initialize(NotEndsWith a) { this.suffix = a.value(); }
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return PatternValidationUtils.notEndsWith(value, suffix);
    }
}
