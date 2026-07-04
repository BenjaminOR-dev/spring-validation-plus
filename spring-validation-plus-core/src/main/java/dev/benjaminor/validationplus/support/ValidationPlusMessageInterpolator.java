package dev.benjaminor.validationplus.support;

import jakarta.validation.MessageInterpolator;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Interpolador de mensajes que soporta placeholders personalizados como {@code {field}} y {@code {other}}.
 */
public class ValidationPlusMessageInterpolator implements MessageInterpolator {

    private final Locale defaultLocale;

    public ValidationPlusMessageInterpolator() {
        this(Locale.getDefault());
    }

    public ValidationPlusMessageInterpolator(Locale defaultLocale) {
        this.defaultLocale = defaultLocale != null ? defaultLocale : Locale.getDefault();
    }

    @Override
    public String interpolate(String messageTemplate, Context context) {
        return interpolate(messageTemplate, context, extractLocale(context));
    }

    @Override
    public String interpolate(String messageTemplate, Context context, Locale locale) {
        Locale effectiveLocale = locale != null ? locale : defaultLocale;
        Map<String, Object> parameters = buildParameters(context);

        String resolvedTemplate = ValidationMessageUtils.resolveTemplate(messageTemplate, effectiveLocale, parameters);
        return ValidationMessageUtils.interpolate(resolvedTemplate, parameters);
    }

    private Map<String, Object> buildParameters(Context context) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("field", ValidationMessageUtils.extractFieldName(context));

        if (context.getConstraintDescriptor() != null && context.getConstraintDescriptor().getAttributes() != null) {
            Map<String, Object> attributes = new HashMap<>(context.getConstraintDescriptor().getAttributes());
            attributes.forEach((key, value) -> {
                if (value != null && !"message".equals(key) && !"groups".equals(key) && !"payload".equals(key)) {
                    parameters.put(key, value);
                }
            });

            if (attributes.containsKey("required")) {
                parameters.put("field", attributes.get("required"));
                parameters.put("other", attributes.get("field"));
            } else if (attributes.containsKey("fields")) {
                Object fields = attributes.get("fields");
                if (fields instanceof String[] fieldNames && fieldNames.length > 0) {
                    parameters.put("other", String.join(", ", fieldNames));
                }
            } else if (attributes.containsKey("other")) {
                parameters.put("field", attributes.get("other"));
                parameters.put("other", attributes.get("field"));
            } else if (attributes.containsKey("confirmation")) {
                String confirmation = String.valueOf(attributes.get("confirmation"));
                if (confirmation.isBlank()) {
                    confirmation = String.valueOf(attributes.get("field")) + "Confirmation";
                }
                parameters.put("field", confirmation);
            } else if (attributes.containsKey("format")
                    && attributes.containsKey("value")
                    && attributes.containsKey("field")) {
                String referenceField = String.valueOf(attributes.get("field"));
                if (referenceField != null && !referenceField.isBlank()) {
                    parameters.put("other", referenceField);
                } else {
                    parameters.put("other", attributes.get("value"));
                }
            }

            Object valueAttribute = attributes.get("value");
            if (valueAttribute instanceof String[] allowedValues) {
                parameters.put("values", String.join(", ", allowedValues));
            }

            Object mimeTypes = attributes.get("mimeTypes");
            if (mimeTypes instanceof String[] allowedMimeTypes && allowedMimeTypes.length > 0) {
                parameters.put("values", String.join(", ", allowedMimeTypes));
            }

            Object allowedExtensions = attributes.get("extensions");
            if (allowedExtensions instanceof String[] extensionValues && extensionValues.length > 0) {
                parameters.put("values", String.join(", ", extensionValues));
            }

            if (attributes.containsKey("min") && attributes.containsKey("max")) {
                parameters.put("min", attributes.get("min"));
                parameters.put("max", attributes.get("max"));
            }
        }

        if (context.getValidatedValue() != null) {
            parameters.put("validatedValue", context.getValidatedValue());
        }

        Object valueParameter = parameters.get("value");
        if (valueParameter != null && !(valueParameter instanceof String[])) {
            parameters.putIfAbsent("min", valueParameter);
            parameters.putIfAbsent("max", valueParameter);
        }

        return parameters;
    }

    private Locale extractLocale(Context context) {
        Object locale = invoke(context, "getLocale");
        if (locale instanceof Locale resolvedLocale) {
            return resolvedLocale;
        }
        return defaultLocale;
    }

    private Object invoke(Object target, String methodName) {
        try {
            return target.getClass().getMethod(methodName).invoke(target);
        } catch (ReflectiveOperationException exception) {
            return null;
        }
    }
}
