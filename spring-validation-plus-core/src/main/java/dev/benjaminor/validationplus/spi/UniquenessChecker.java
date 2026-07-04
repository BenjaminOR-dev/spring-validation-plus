package dev.benjaminor.validationplus.spi;

/**
 * SPI for checking whether a value is unique against an external data source.
 */
public interface UniquenessChecker {

    /**
     * Returns whether the value is unique in the given context.
     */
    boolean isUnique(UniqueCheckRequest request);
}
