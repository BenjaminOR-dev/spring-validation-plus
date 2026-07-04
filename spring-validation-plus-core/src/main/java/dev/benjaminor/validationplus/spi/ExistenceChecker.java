package dev.benjaminor.validationplus.spi;

/**
 * SPI for checking whether a value exists in an external data source.
 */
public interface ExistenceChecker {

    /**
     * Returns whether the value exists in the given context.
     */
    boolean exists(ExistenceCheckRequest request);
}
