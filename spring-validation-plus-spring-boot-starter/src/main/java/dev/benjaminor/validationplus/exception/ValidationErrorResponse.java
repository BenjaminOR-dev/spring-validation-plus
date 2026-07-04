package dev.benjaminor.validationplus.exception;

import java.util.List;
import java.util.Map;

/**
 * Respuesta estándar de errores de validación estilo Laravel.
 *
 * @param errors mapa de campo a lista de mensajes
 */
public record ValidationErrorResponse(Map<String, List<String>> errors) {
}
