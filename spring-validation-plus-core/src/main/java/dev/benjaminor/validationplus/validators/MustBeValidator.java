package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.MustBe;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Locale;
import java.util.Objects;

/**
 * Validator for {@link MustBe}.
 */
public class MustBeValidator implements ConstraintValidator<MustBe, Object> {

    private String expected;
    private boolean ignoreCase;

    @Override
    public void initialize(MustBe constraintAnnotation) {
        this.expected = constraintAnnotation.value();
        this.ignoreCase = constraintAnnotation.ignoreCase();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        String actual = String.valueOf(value).trim();
        String target = expected == null ? "" : expected.trim();
        if (ignoreCase) {
            return Objects.equals(
                    actual.toLowerCase(Locale.ROOT),
                    target.toLowerCase(Locale.ROOT));
        }
        return Objects.equals(actual, target);
    }
}
