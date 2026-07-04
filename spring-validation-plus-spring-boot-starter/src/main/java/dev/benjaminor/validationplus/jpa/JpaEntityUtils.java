package dev.benjaminor.validationplus.jpa;

import jakarta.persistence.Entity;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * JPA utilities for uniqueness and existence validations.
 */
public final class JpaEntityUtils {

    private JpaEntityUtils() {
    }

    public static String resolveEntityName(Class<?> entityClass) {
        Entity entity = entityClass.getAnnotation(Entity.class);
        if (entity != null && entity.name() != null && !entity.name().isBlank()) {
            return entity.name();
        }
        return entityClass.getSimpleName();
    }

    public static String buildEqualityClause(String column, String parameterName, boolean ignoreCase, Object value) {
        if (ignoreCase && value instanceof CharSequence) {
            return "LOWER(e.%s) = LOWER(:%s)".formatted(column, parameterName);
        }
        return "e.%s = :%s".formatted(column, parameterName);
    }

    public static Object normalizeValue(Object value) {
        if (value instanceof CharSequence charSequence) {
            return charSequence.toString().trim();
        }
        return value;
    }

    public static Object convertId(Class<?> entityClass, String idColumn, Object rawId) {
        Field field = findField(entityClass, idColumn);
        if (field == null) {
            return rawId;
        }
        Class<?> targetType = field.getType();
        if (targetType.isInstance(rawId)) {
            return rawId;
        }
        if (targetType.equals(Long.class) || targetType.equals(long.class)) {
            return Long.valueOf(String.valueOf(rawId));
        }
        if (targetType.equals(Integer.class) || targetType.equals(int.class)) {
            return Integer.valueOf(String.valueOf(rawId));
        }
        return rawId;
    }

    private static Field findField(Class<?> type, String fieldName) {
        Class<?> current = type;
        while (current != null && current != Object.class) {
            try {
                Field field = current.getDeclaredField(fieldName);
                if (!Modifier.isStatic(field.getModifiers())) {
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
