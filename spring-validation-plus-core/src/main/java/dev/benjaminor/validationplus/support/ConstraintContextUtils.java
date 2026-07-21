package dev.benjaminor.validationplus.support;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

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
        Path basePath = getBasePath(context);
        if (basePath == null) {
            return null;
        }

        Path.Node leafNode = leafNode(basePath);
        if (leafNode != null && leafNode.getKind() == ElementKind.PROPERTY) {
            return leafNode.getName();
        }
        return null;
    }

    /**
     * Resolves the leaf node on both Hibernate Validator 8 ({@code PathImpl}) and 9 ({@code MutablePath}).
     */
    private static Path.Node leafNode(Path path) {
        try {
            Method getLeafNode = path.getClass().getMethod("getLeafNode");
            Object leaf = getLeafNode.invoke(path);
            if (leaf instanceof Path.Node node) {
                return node;
            }
        } catch (ReflectiveOperationException ignored) {
            // Fall through to iteration.
        }
        Path.Node last = null;
        for (Path.Node node : path) {
            last = node;
        }
        return last;
    }

    private static Path getBasePath(ConstraintValidatorContext context) {
        ConstraintValidatorContextImpl hibernateContext = unwrapContext(context);
        if (hibernateContext == null) {
            return null;
        }
        try {
            Field basePathField = ConstraintValidatorContextImpl.class.getDeclaredField("basePath");
            basePathField.setAccessible(true);
            Object value = basePathField.get(hibernateContext);
            return value instanceof Path path ? path : null;
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
