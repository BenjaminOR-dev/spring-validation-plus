package dev.benjaminor.validationplus.support;

import dev.benjaminor.validationplus.constraints.ConditionalOperator;
import jakarta.validation.MessageInterpolator;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Message interpolator that supports custom placeholders such as {@code {field}} and {@code {other}}.
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
        enrichConditionalMessage(parameters, context, effectiveLocale);

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

            enrichCrossFieldParameters(parameters, attributes);

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

    private void enrichConditionalMessage(Map<String, Object> parameters, Context context, Locale locale) {
        if (context.getConstraintDescriptor() == null || context.getConstraintDescriptor().getAttributes() == null) {
            return;
        }

        Map<String, Object> attributes = context.getConstraintDescriptor().getAttributes();
        if (!attributes.containsKey("operator")) {
            return;
        }

        Object operatorAttribute = attributes.get("operator");
        ConditionalOperator operator = operatorAttribute instanceof ConditionalOperator resolvedOperator
                ? resolvedOperator
                : ConditionalOperator.EQUALS;
        parameters.put(
                "condition",
                ConditionalMessageSupport.conditionPhrase(operator, locale, isUnlessConstraint(context)));

        if (operator == ConditionalOperator.IN) {
            Object valueParameter = parameters.get("value");
            if (valueParameter instanceof String csv) {
                parameters.put("value", ValidationMessageUtils.formatCommaSeparatedList(csv));
            }
        }
    }

    private boolean isUnlessConstraint(Context context) {
        if (context.getConstraintDescriptor().getAnnotation() == null) {
            return false;
        }
        return context.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName().endsWith("Unless");
    }

    private void enrichCrossFieldParameters(Map<String, Object> parameters, Map<String, Object> attributes) {
        Object required = attributes.get("required");
        Object missing = attributes.get("missing");
        Object prohibited = attributes.get("prohibited");
        Object field = attributes.get("field");
        Object other = attributes.get("other");

        Object explicitTarget = firstNonBlank(required, missing, prohibited);
        if (isNonBlank(explicitTarget)) {
            parameters.put("field", explicitTarget);
            String[] observedFieldsForTarget = observedFieldNames(attributes);
            if (observedFieldsForTarget.length > 0) {
                parameters.put("other", String.join(", ", observedFieldsForTarget));
            } else if (isNonBlank(field)) {
                parameters.put("other", field);
            }
            return;
        }

        if (attributes.containsKey("confirmation")) {
            String referenceField = CrossFieldConstraintSupport.firstNonBlank(
                    field != null ? String.valueOf(field) : "",
                    attributes.get("value") != null ? String.valueOf(attributes.get("value")) : "");
            if (isNonBlank(referenceField)) {
                parameters.put("field", referenceField);
            }
            return;
        }

        String[] observedFields = observedFieldNames(attributes);
        if (observedFields.length > 0) {
            parameters.put("other", String.join(", ", observedFields));
            return;
        }

        if (isNonBlank(other) && isNonBlank(field)) {
            parameters.put("field", other);
            parameters.put("other", field);
            return;
        }

        if (isNonBlank(field) && isBlank(other)) {
            parameters.put("other", field);
            return;
        }

        if (attributes.get("value") instanceof String stringValue
                && isNonBlank(stringValue)
                && !attributes.containsKey("fields")
                && isBlank(other)
                && !attributes.containsKey("format")) {
            parameters.put("other", stringValue);
            return;
        }

        if (attributes.containsKey("format") && attributes.containsKey("value") && attributes.containsKey("field")) {
            String referenceField = String.valueOf(field);
            if (isNonBlank(referenceField)) {
                parameters.put("other", referenceField);
            } else if (isNonBlank(attributes.get("value"))) {
                parameters.put("other", attributes.get("value"));
            }
        }
    }

    private String[] observedFieldNames(Map<String, Object> attributes) {
        Object fields = attributes.get("fields");
        if (fields instanceof String[] fieldNames && fieldNames.length > 0) {
            return fieldNames;
        }
        Object value = attributes.get("value");
        if (value instanceof String[] valueFields && valueFields.length > 0) {
            return valueFields;
        }
        return new String[0];
    }

    private Object firstNonBlank(Object... values) {
        if (values == null) {
            return null;
        }
        for (Object value : values) {
            if (isNonBlank(value)) {
                return value;
            }
        }
        return null;
    }

    private boolean isBlank(Object value) {
        return value == null || String.valueOf(value).isBlank();
    }

    private boolean isNonBlank(Object value) {
        return value != null && !String.valueOf(value).isBlank();
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
