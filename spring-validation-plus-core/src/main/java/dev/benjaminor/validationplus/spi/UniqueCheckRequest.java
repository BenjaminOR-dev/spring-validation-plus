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
        boolean ignoreCase,
        String persistenceUnit
) {

    /**
     * Compatibility constructor — empty persistence unit (primary EMF).
     */
    public UniqueCheckRequest(
            Class<?> entity,
            String column,
            Object value,
            Object excludeId,
            String excludeColumn,
            boolean ignoreCase) {
        this(entity, column, value, excludeId, excludeColumn, ignoreCase, "");
    }

    public UniqueCheckRequest {
        if (persistenceUnit == null) {
            persistenceUnit = "";
        }
    }
}
