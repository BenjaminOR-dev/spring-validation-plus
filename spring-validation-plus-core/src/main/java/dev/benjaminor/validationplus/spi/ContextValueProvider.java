package dev.benjaminor.validationplus.spi;

import java.util.Optional;

/**
 * Provides request context values (path variables, query parameters, etc.).
 * <p>
 * The Spring Boot starter registers an implementation that reads path variables
 * when the application runs in a servlet container.
 */
public interface ContextValueProvider {

    Optional<Object> get(String key);
}
