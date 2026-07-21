package dev.benjaminor.validationplus.spi;

/**
 * Context for an {@code @Exists} existence check.
 */
public record ExistenceCheckRequest(
        Class<?> entity,
        String column,
        Object value,
        boolean ignoreCase,
        String persistenceUnit
) {

    /**
     * Compatibility constructor — empty persistence unit (primary EMF).
     */
    public ExistenceCheckRequest(Class<?> entity, String column, Object value, boolean ignoreCase) {
        this(entity, column, value, ignoreCase, "");
    }

    public ExistenceCheckRequest {
        if (persistenceUnit == null) {
            persistenceUnit = "";
        }
    }
}
