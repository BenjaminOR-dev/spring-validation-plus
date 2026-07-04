package dev.benjaminor.validationplus.web;

import dev.benjaminor.validationplus.spi.ContextValueProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;
import java.util.Optional;

/**
 * Resuelve valores desde path variables y query params de la petición HTTP actual.
 */
public class RequestContextValueProvider implements ContextValueProvider {

    @Override
    public Optional<Object> get(String key) {
        if (key == null || key.isBlank()) {
            return Optional.empty();
        }

        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes servletAttributes)) {
            return Optional.empty();
        }

        HttpServletRequest request = servletAttributes.getRequest();

        Object uriVariables = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (uriVariables instanceof Map<?, ?> variables && variables.containsKey(key)) {
            Object value = variables.get(key);
            if (value != null) {
                return Optional.of(value);
            }
        }

        String parameter = request.getParameter(key);
        if (parameter != null && !parameter.isBlank()) {
            return Optional.of(parameter);
        }

        return Optional.empty();
    }
}
