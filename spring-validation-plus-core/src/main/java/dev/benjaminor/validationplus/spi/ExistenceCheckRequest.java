package dev.benjaminor.validationplus.spi;

/**
 * Contexto de una comprobación de existencia para {@code @Exists}.
 */
public record ExistenceCheckRequest(
        Class<?> entity,
        String column,
        Object value,
        boolean ignoreCase
) {
}
