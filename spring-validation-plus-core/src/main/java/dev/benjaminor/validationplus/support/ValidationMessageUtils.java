package dev.benjaminor.validationplus.support;

import jakarta.validation.MessageInterpolator;
import jakarta.validation.Path;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities for resolving validation messages from {@code ValidationMessages*.properties} files.
 *
 * <p>Application overrides may ship a partial {@code ValidationMessages*.properties}. Java
 * {@link java.util.ResourceBundle} does not merge the same basename across JARs, so a partial app
 * bundle would otherwise hide library defaults. This class resolves each key as:
 * <ol>
 *   <li>app classpath override (non-library {@code ValidationMessages*.properties}), if present</li>
 *   <li>library defaults from this module's own {@code ValidationMessages*.properties}</li>
 * </ol>
 */
public final class ValidationMessageUtils {

    public static final String BUNDLE_BASENAME = "ValidationMessages";

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{([^}]+)}");

    private static final ConcurrentMap<String, Properties> PROPERTIES_BY_URL = new ConcurrentHashMap<>();

    private ValidationMessageUtils() {
    }

    /**
     * Resolves a message key using the standard Jakarta Validation bundle.
     */
    public static String resolve(String messageKey, Locale locale, Map<String, Object> parameters) {
        String template = lookupKey(messageKey, locale);
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
                resolved = lookupKey(key, locale);
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

    static String lookupKey(String messageKey, Locale locale) {
        Locale effectiveLocale = locale != null ? locale : Locale.getDefault();
        String override = findMessage(messageKey, effectiveLocale, true);
        if (override != null) {
            return override;
        }
        String library = findMessage(messageKey, effectiveLocale, false);
        if (library != null) {
            return library;
        }
        throw new MissingResourceException(
                "Can't find resource for bundle " + BUNDLE_BASENAME + ", key " + messageKey,
                BUNDLE_BASENAME,
                messageKey);
    }

    private static String findMessage(String messageKey, Locale locale, boolean appOverrideOnly) {
        ClassLoader classLoader = overrideClassLoader();
        for (String resourceName : candidateResourceNames(locale)) {
            Enumeration<URL> resources;
            try {
                resources = classLoader.getResources(resourceName);
            } catch (IOException exception) {
                continue;
            }
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                boolean fromLibrary = isFromLibraryClasspath(url);
                if (appOverrideOnly && fromLibrary) {
                    continue;
                }
                if (!appOverrideOnly && !fromLibrary) {
                    continue;
                }
                Properties properties = loadProperties(url);
                if (properties.containsKey(messageKey)) {
                    return properties.getProperty(messageKey);
                }
            }
        }
        return null;
    }

    private static List<String> candidateResourceNames(Locale locale) {
        List<String> names = new ArrayList<>(3);
        String language = locale.getLanguage();
        String country = locale.getCountry();
        if (!language.isEmpty() && !country.isEmpty()) {
            names.add(BUNDLE_BASENAME + '_' + language + '_' + country + ".properties");
        }
        if (!language.isEmpty()) {
            names.add(BUNDLE_BASENAME + '_' + language + ".properties");
        }
        names.add(BUNDLE_BASENAME + ".properties");
        return names;
    }

    private static ClassLoader overrideClassLoader() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        return contextClassLoader != null ? contextClassLoader : ValidationMessageUtils.class.getClassLoader();
    }

    private static boolean isFromLibraryClasspath(URL resourceUrl) {
        CodeSource codeSource = ValidationMessageUtils.class.getProtectionDomain().getCodeSource();
        if (codeSource == null || codeSource.getLocation() == null || resourceUrl == null) {
            return false;
        }
        String libraryLocation = codeSource.getLocation().toExternalForm();
        String resourceLocation = resourceUrl.toExternalForm();
        if (resourceLocation.startsWith(libraryLocation)) {
            return true;
        }
        // Nested Spring Boot jars: jar:file:/app.jar!/BOOT-INF/lib/core.jar!/ValidationMessages.properties
        int libraryJarIndex = libraryLocation.lastIndexOf('/');
        String libraryFileName = libraryJarIndex >= 0 ? libraryLocation.substring(libraryJarIndex + 1) : libraryLocation;
        if (!libraryFileName.isEmpty() && resourceLocation.contains(libraryFileName)) {
            return true;
        }
        // Exploded module tests: .../spring-validation-plus-core/target/classes/
        return resourceLocation.contains("/spring-validation-plus-core/")
                && resourceLocation.contains('/' + BUNDLE_BASENAME);
    }

    private static Properties loadProperties(URL url) {
        return PROPERTIES_BY_URL.computeIfAbsent(url.toExternalForm(), ignored -> {
            Properties properties = new Properties();
            try (InputStream inputStream = url.openStream();
                    InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                properties.load(reader);
            } catch (IOException exception) {
                return properties;
            }
            return properties;
        });
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

    private static Object invoke(Object target, String methodName) {
        try {
            return target.getClass().getMethod(methodName).invoke(target);
        } catch (ReflectiveOperationException exception) {
            return null;
        }
    }
}
