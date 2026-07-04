package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.NotStartsWith;
import dev.benjaminor.validationplus.support.PatternValidationUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotStartsWithValidator implements ConstraintValidator<NotStartsWith, Object> {
    private String prefix;
    @Override
    public void initialize(NotStartsWith a) { this.prefix = a.value(); }
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return PatternValidationUtils.notStartsWith(value, prefix);
    }
}
