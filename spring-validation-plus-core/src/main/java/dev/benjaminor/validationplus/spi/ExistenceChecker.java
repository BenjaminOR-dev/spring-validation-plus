package dev.benjaminor.validationplus.spi;

/**
 * SPI para comprobar que un valor existe en un origen de datos externo.
 */
public interface ExistenceChecker {

    /**
     * Indica si el valor existe en el contexto indicado.
     */
    boolean exists(ExistenceCheckRequest request);
}
