package dev.benjaminor.validationplus.spi;

/**
 * Context for a {@code @Unique} uniqueness check.
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
