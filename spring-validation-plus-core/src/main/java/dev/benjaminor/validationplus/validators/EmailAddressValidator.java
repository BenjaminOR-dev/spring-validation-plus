package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.EmailAddress;
import dev.benjaminor.validationplus.support.EmptyUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Validator for {@link EmailAddress}.
 */
public class EmailAddressValidator implements ConstraintValidator<EmailAddress, CharSequence> {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[\\w.!#$%&'*+/=?^`{|}~-]+@[\\w-]+(?:\\.[\\w-]+)+$",
            Pattern.CASE_INSENSITIVE);

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (EmptyUtils.isBlank(value)) {
            return true;
        }
        return EMAIL_PATTERN.matcher(value.toString().trim()).matches();
    }
}
