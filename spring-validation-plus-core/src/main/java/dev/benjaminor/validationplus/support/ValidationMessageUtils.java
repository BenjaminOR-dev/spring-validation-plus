package dev.benjaminor.validationplus.support;

import jakarta.validation.MessageInterpolator;

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
                    replacement != null ? String.valueOf(replacement) : matcher.group(0)));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    /**
     * Extracts the field name from the interpolator context.
     */
    public static String extractFieldName(MessageInterpolator.Context context) {
        if (context == null) {
            return "";
        }
        Object propertyPath = invoke(context, "getPropertyPath");
        if (propertyPath == null) {
            return "";
        }
        Object leafNode = invoke(propertyPath, "getLeafNode");
        if (leafNode == null) {
            return "";
        }
        Object name = invoke(leafNode, "getName");
        return name != null ? String.valueOf(name) : "";
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
