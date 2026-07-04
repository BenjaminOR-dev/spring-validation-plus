package dev.benjaminor.validationplus.spi;

/**
 * SPI para comprobar unicidad de un valor contra un origen de datos externo.
 */
public interface UniquenessChecker {

    /**
     * Indica si el valor es único en el contexto indicado.
     */
    boolean isUnique(UniqueCheckRequest request);
}
