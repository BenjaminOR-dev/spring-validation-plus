package dev.benjaminor.validationplus.support;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;

import java.lang.reflect.Field;

/**
 * Utilities for accessing the validation context at runtime.
 */
public final class ConstraintContextUtils {

    private ConstraintContextUtils() {
    }

    /**
     * Returns the root bean when the validation engine exposes it (for example, Hibernate Validator).
     */
    public static Object getRootBean(ConstraintValidatorContext context) {
        Object runtimeRoot = CrossFieldValidationRuntime.getRootBean();
        if (runtimeRoot != null) {
            return runtimeRoot;
        }
        return null;
    }

    /**
     * Returns the validated property name when the engine exposes it (for example, Hibernate Validator).
     */
    public static String getPropertyName(ConstraintValidatorContext context) {
        PathImpl basePath = getBasePath(context);
        if (basePath == null || basePath.getLeafNode() == null) {
            return null;
        }
        Path.Node leafNode = basePath.getLeafNode();
        if (leafNode.getKind() == ElementKind.PROPERTY) {
            return leafNode.getName();
        }
        return null;
    }

    private static PathImpl getBasePath(ConstraintValidatorContext context) {
        ConstraintValidatorContextImpl hibernateContext = unwrapContext(context);
        if (hibernateContext == null) {
            return null;
        }
        try {
            Field basePathField = ConstraintValidatorContextImpl.class.getDeclaredField("basePath");
            basePathField.setAccessible(true);
            return (PathImpl) basePathField.get(hibernateContext);
        } catch (ReflectiveOperationException exception) {
            return null;
        }
    }

    private static ConstraintValidatorContextImpl unwrapContext(ConstraintValidatorContext context) {
        if (context instanceof ConstraintValidatorContextImpl impl) {
            return impl;
        }
        if (context == null) {
            return null;
        }
        try {
            HibernateConstraintValidatorContext hibernateContext =
                    context.unwrap(HibernateConstraintValidatorContext.class);
            if (hibernateContext instanceof ConstraintValidatorContextImpl impl) {
                return impl;
            }
        } catch (IllegalArgumentException ignored) {
            // Not a Hibernate Validator context.
        }
        return null;
    }
}
