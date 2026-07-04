package dev.benjaminor.validationplus.spi;

/**
 * Context for an {@code @Exists} existence check.
 */
public record ExistenceCheckRequest(
        Class<?> entity,
        String column,
        Object value,
        boolean ignoreCase
) {
}
