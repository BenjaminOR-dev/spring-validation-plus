package dev.benjaminor.validationplus.exception;

import java.util.List;
import java.util.Map;

/**
 * Standard Laravel-style validation error response.
 *
 * @param errors mapa de campo a lista de mensajes
 */
public record ValidationErrorResponse(Map<String, List<String>> errors) {
}
