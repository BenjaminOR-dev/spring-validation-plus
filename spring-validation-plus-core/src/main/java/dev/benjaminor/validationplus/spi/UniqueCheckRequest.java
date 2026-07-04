package dev.benjaminor.validationplus.spi;

/**
 * Contexto de una comprobación de unicidad para {@code @Unique}.
 */
public record UniqueCheckRequest(
        Class<?> entity,
        String column,
        Object value,
        Object excludeId,
        String excludeColumn,
        boolean ignoreCase
) {
}
