package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Regex;
import dev.benjaminor.validationplus.support.PatternValidationUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Validator para {@link Regex}.
 */
public class RegexValidator implements ConstraintValidator<Regex, Object> {

    private Pattern pattern;

    @Override
    public void initialize(Regex constraintAnnotation) {
        this.pattern = Pattern.compile(constraintAnnotation.pattern(), constraintAnnotation.flags());
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return PatternValidationUtils.matchesRegex(value, pattern);
    }
}
