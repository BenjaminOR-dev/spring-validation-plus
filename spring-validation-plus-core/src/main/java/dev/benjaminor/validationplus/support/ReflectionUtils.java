package dev.benjaminor.validationplus.support;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Reflection utilities for class-level validations.
 */
public final class ReflectionUtils {

    private ReflectionUtils() {
    }

    /**
     * Reads a field value by name, including inherited fields.
     *
     * @param target    instancia objetivo
     * @param fieldName field name
     * @return field value, or {@code null} if it does not exist
     */
    public static Object getFieldValue(Object target, String fieldName) {
        Field field = findField(target.getClass(), fieldName);
        if (field == null) {
            return null;
        }
        try {
            return field.get(target);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Unable to read field '" + fieldName + "'", exception);
        }
    }

    private static Field findField(Class<?> type, String fieldName) {
        Class<?> current = type;
        while (current != null && current != Object.class) {
            try {
                Field field = current.getDeclaredField(fieldName);
                if (!Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    return field;
                }
            } catch (NoSuchFieldException ignored) {
                // Continue searching in superclass.
            }
            current = current.getSuperclass();
        }
        return null;
    }
}
