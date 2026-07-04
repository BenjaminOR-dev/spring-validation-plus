package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.NotRegex;
import dev.benjaminor.validationplus.support.PatternValidationUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Validator for {@link NotRegex}.
 */
public class NotRegexValidator implements ConstraintValidator<NotRegex, Object> {

    private Pattern pattern;

    @Override
    public void initialize(NotRegex constraintAnnotation) {
        this.pattern = Pattern.compile(constraintAnnotation.pattern(), constraintAnnotation.flags());
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return PatternValidationUtils.notMatchesRegex(value, pattern);
    }
}
