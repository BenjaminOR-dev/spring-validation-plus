package dev.benjaminor.validationplus.spi;

import java.util.Optional;

/**
 * Provee valores del contexto de la petición (path variables, query params, etc.).
 * <p>
 * El starter de Spring Boot registra una implementación que lee variables de ruta
 * cuando la app corre en un servlet container.
 */
public interface ContextValueProvider {

    Optional<Object> get(String key);
}
