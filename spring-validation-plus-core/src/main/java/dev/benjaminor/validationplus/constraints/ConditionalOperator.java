package dev.benjaminor.validationplus.constraints;

/**
 * Comparison operator for conditional cross-field constraints ({@code *If}, {@code *Unless}).
 */
public enum ConditionalOperator {

    /**
     * The observed field equals {@code value} (default).
     */
    EQUALS,

    /**
     * The observed field does not equal {@code value}.
     */
    NOT_EQUALS,

    /**
     * The observed field equals one of the comma-separated values in {@code value}.
     */
    IN
}
