package dev.benjaminor.validationplus.support;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.executable.ExecutableValidator;
import jakarta.validation.metadata.BeanDescriptor;

import java.util.Set;

/**
 * Delegating validator that exposes the validated root bean to field-level cross-field constraints.
 */
public final class CrossFieldValidationRuntimeValidator implements Validator {

    private final Validator delegate;

    private CrossFieldValidationRuntimeValidator(Validator delegate) {
        this.delegate = delegate;
    }

    public static Validator wrap(Validator delegate) {
        if (delegate == null || delegate instanceof CrossFieldValidationRuntimeValidator) {
            return delegate;
        }
        return new CrossFieldValidationRuntimeValidator(delegate);
    }

    @Override
    public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
        return withRoot(object, () -> delegate.validate(object, groups));
    }

    @Override
    public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Class<?>... groups) {
        return withRoot(object, () -> delegate.validateProperty(object, propertyName, groups));
    }

    @Override
    public <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName, Object value, Class<?>... groups) {
        return delegate.validateValue(beanType, propertyName, value, groups);
    }

    @Override
    public BeanDescriptor getConstraintsForClass(Class<?> clazz) {
        return delegate.getConstraintsForClass(clazz);
    }

    @Override
    public <T> T unwrap(Class<T> type) {
        return delegate.unwrap(type);
    }

    @Override
    public ExecutableValidator forExecutables() {
        return delegate.forExecutables();
    }

    private <T> Set<ConstraintViolation<T>> withRoot(Object root, ValidationCall<T> call) {
        CrossFieldValidationRuntime.setRootBean(root);
        try {
            return call.run();
        } finally {
            CrossFieldValidationRuntime.clearRootBean();
        }
    }

    @FunctionalInterface
    private interface ValidationCall<T> {
        Set<ConstraintViolation<T>> run();
    }
}
