package dev.benjaminor.validationplus.support;

import jakarta.validation.MessageInterpolator;
import jakarta.validation.Path;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities for resolving validation messages from {@code ValidationMessages*.properties} files.
 */
public final class ValidationMessageUtils {

    public static final String BUNDLE_BASENAME = "ValidationMessages";

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{([^}]+)}");

    private ValidationMessageUtils() {
    }

    /**
     * Resolves a message key using the standard Jakarta Validation bundle.
     */
    public static String resolve(String messageKey, Locale locale, Map<String, Object> parameters) {
        String template = getBundle(locale).getString(messageKey);
        return interpolate(template, parameters);
    }

    /**
     * Resolves a message template, including brace-delimited keys.
     */
    public static String resolveTemplate(String template, Locale locale, Map<String, Object> parameters) {
        String resolved = template;
        if (template.startsWith("{") && template.endsWith("}")) {
            String key = template.substring(1, template.length() - 1);
            try {
                resolved = getBundle(locale).getString(key);
            } catch (MissingResourceException ignored) {
                resolved = template;
            }
        }
        return interpolate(resolved, parameters);
    }

    /**
     * Interpolates placeholders in a message template.
     */
    public static String interpolate(String template, Map<String, Object> parameters) {
        if (template == null || parameters == null || parameters.isEmpty()) {
            return template;
        }
        Matcher matcher = PLACEHOLDER.matcher(template);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String name = matcher.group(1);
            Object replacement = parameters.get(name);
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(
                    replacement != null ? formatParameterValue(replacement) : matcher.group(0)));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    /**
     * Formats constraint parameter values for user-facing messages.
     * Whole numbers such as {@code 1.0} are rendered as {@code 1}.
     * Arrays are joined with {@code ", "}.
     */
    public static String formatParameterValue(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof String[] stringValues) {
            return String.join(", ", stringValues);
        }
        if (value instanceof Object[] objectValues) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < objectValues.length; i++) {
                if (i > 0) {
                    builder.append(", ");
                }
                builder.append(formatParameterValue(objectValues[i]));
            }
            return builder.toString();
        }
        if (value instanceof BigDecimal decimal) {
            BigDecimal normalized = decimal.stripTrailingZeros();
            if (normalized.scale() <= 0) {
                return normalized.toBigInteger().toString();
            }
            return normalized.toPlainString();
        }
        if (value instanceof Number number) {
            double asDouble = number.doubleValue();
            if (Double.isFinite(asDouble) && asDouble == Math.rint(asDouble)) {
                return String.valueOf((long) asDouble);
            }
            return number.toString();
        }
        return String.valueOf(value);
    }

    /**
     * Normalizes {@code "A,B"} / {@code "A, B"} into {@code "A, B"} for readable IN-list messages.
     */
    public static String formatCommaSeparatedList(String value) {
        if (value == null || value.isBlank() || value.indexOf(',') < 0) {
            return value == null ? "" : value;
        }
        String[] parts = value.split(",");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(trimmed);
        }
        return builder.length() > 0 ? builder.toString() : value;
    }

    /**
     * Extracts the field name from the interpolator context.
     *
     * <p>Uses the public {@link jakarta.validation.Path} iteration API. Hibernate Validator 9 stores the
     * path as a package-private {@code MaterializedPath}; reflecting {@code getLeafNode()} on that class
     * throws {@link IllegalAccessException} and must not be relied upon.
     */
    public static String extractFieldName(MessageInterpolator.Context context) {
        if (context == null) {
            return "";
        }
        Object propertyPath = invoke(context, "getPropertyPath");
        if (propertyPath == null) {
            return "";
        }

        String fromNodes = lastNamedNode(propertyPath);
        if (!fromNodes.isBlank()) {
            return fromNodes;
        }

        String asString = String.valueOf(propertyPath);
        if (asString.isBlank() || "null".equals(asString)) {
            return "";
        }
        int separator = Math.max(asString.lastIndexOf('.'), asString.lastIndexOf('/'));
        String leaf = separator >= 0 ? asString.substring(separator + 1) : asString;
        int bracket = leaf.indexOf('[');
        return bracket >= 0 ? leaf.substring(0, bracket) : leaf;
    }

    private static String lastNamedNode(Object propertyPath) {
        if (!(propertyPath instanceof Iterable<?> nodes)) {
            return "";
        }
        String lastName = "";
        for (Object node : nodes) {
            if (node instanceof Path.Node pathNode) {
                String name = pathNode.getName();
                if (name != null && !name.isBlank()) {
                    lastName = name;
                }
            }
        }
        return lastName;
    }

    private static ResourceBundle getBundle(Locale locale) {
        Locale effectiveLocale = locale != null ? locale : Locale.getDefault();
        return ResourceBundle.getBundle(BUNDLE_BASENAME, effectiveLocale, ValidationMessageUtils.class.getClassLoader());
    }

    private static Object invoke(Object target, String methodName) {
        try {
            return target.getClass().getMethod(methodName).invoke(target);
        } catch (ReflectiveOperationException exception) {
            return null;
        }
    }
}
