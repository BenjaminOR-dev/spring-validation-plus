package dev.benjaminor.validationplus.support;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.hibernate.validator.HibernateValidator;

import java.util.Locale;

/**
 * Utilidad para crear validators en tests.
 */
public final class ValidatorTestSupport {

    private ValidatorTestSupport() {
    }

    public static Validator createValidator() {
        return createValidator(Locale.ENGLISH);
    }

    public static Validator createValidator(Locale locale) {
        Validator validator = Validation.byProvider(HibernateValidator.class)
                .configure()
                .defaultLocale(locale)
                .messageInterpolator(new ValidationPlusMessageInterpolator(locale))
                .buildValidatorFactory()
                .getValidator();
        return CrossFieldValidationRuntimeValidator.wrap(validator);
    }
}
