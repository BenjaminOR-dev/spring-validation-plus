package dev.benjaminor.validationplus.support;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Utilidades de reflexión para validaciones a nivel clase.
 */
public final class ReflectionUtils {

    private ReflectionUtils() {
    }

    /**
     * Lee el valor de un campo por nombre, incluyendo campos heredados.
     *
     * @param target    instancia objetivo
     * @param fieldName nombre del campo
     * @return valor del campo o {@code null} si no existe
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
