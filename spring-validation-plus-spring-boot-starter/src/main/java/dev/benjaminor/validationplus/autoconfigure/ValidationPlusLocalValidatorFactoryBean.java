package dev.benjaminor.validationplus.autoconfigure;

import dev.benjaminor.validationplus.support.CrossFieldValidationRuntime;
import dev.benjaminor.validationplus.support.CrossFieldValidationRuntimeValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Set;

/**
 * Local validator factory that exposes the validated root bean to field-level cross-field constraints.
 */
public class ValidationPlusLocalValidatorFactoryBean extends LocalValidatorFactoryBean {

    @Override
    public Validator getValidator() {
        return CrossFieldValidationRuntimeValidator.wrap(super.getValidator());
    }

    @Override
    public void validate(Object target, Errors errors) {
        withRoot(target, () -> {
            super.validate(target, errors);
            return null;
        });
    }

    @Override
    public void validate(Object target, Errors errors, Object... validationHints) {
        withRoot(target, () -> {
            super.validate(target, errors, validationHints);
            return null;
        });
    }

    @Override
    public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
        return withRoot(object, () -> super.validate(object, groups));
    }

    @Override
    public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Class<?>... groups) {
        return withRoot(object, () -> super.validateProperty(object, propertyName, groups));
    }

    private <T> T withRoot(Object root, RootValidationCall<T> call) {
        CrossFieldValidationRuntime.setRootBean(root);
        try {
            return call.run();
        } finally {
            CrossFieldValidationRuntime.clearRootBean();
        }
    }

    @FunctionalInterface
    private interface RootValidationCall<T> {
        T run();
    }
}
