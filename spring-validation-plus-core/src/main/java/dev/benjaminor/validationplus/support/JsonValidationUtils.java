package dev.benjaminor.validationplus.support;

/**
 * Utilities for {@code @Json}.
 */
public final class JsonValidationUtils {

    private JsonValidationUtils() {
    }

    public static boolean isValidJson(Object value) {
        if (EmptyUtils.isBlank(value)) {
            return true;
        }
        if (!(value instanceof CharSequence charSequence)) {
            return false;
        }
        return isValidJson(charSequence.toString().trim());
    }

    public static boolean isValidJson(String value) {
        if (value == null || value.isBlank()) {
            return true;
        }
        try {
            int end = parseValue(value, skipWhitespace(value, 0));
            return skipWhitespace(value, end) == value.length();
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private static int parseValue(String json, int index) {
        index = skipWhitespace(json, index);
        if (index >= json.length()) {
            throw new IllegalArgumentException("Unexpected end of JSON");
        }
        return switch (json.charAt(index)) {
            case '{' -> parseObject(json, index);
            case '[' -> parseArray(json, index);
            case '"' -> parseString(json, index);
            case 't', 'f', 'n' -> parseLiteral(json, index);
            default -> parseNumber(json, index);
        };
    }

    private static int parseObject(String json, int start) {
        int index = skipWhitespace(json, start + 1);
        if (index < json.length() && json.charAt(index) == '}') {
            return index + 1;
        }
        while (index < json.length()) {
            index = parseString(json, index);
            index = skipWhitespace(json, index);
            if (index >= json.length() || json.charAt(index) != ':') {
                throw new IllegalArgumentException("Expected ':'");
            }
            index = parseValue(json, index + 1);
            index = skipWhitespace(json, index);
            if (index >= json.length()) {
                throw new IllegalArgumentException("Unclosed object");
            }
            if (json.charAt(index) == '}') {
                return index + 1;
            }
            if (json.charAt(index) != ',') {
                throw new IllegalArgumentException("Expected ',' or '}'");
            }
            index = skipWhitespace(json, index + 1);
        }
        throw new IllegalArgumentException("Unclosed object");
    }

    private static int parseArray(String json, int start) {
        int index = skipWhitespace(json, start + 1);
        if (index < json.length() && json.charAt(index) == ']') {
            return index + 1;
        }
        while (index < json.length()) {
            index = parseValue(json, index);
            index = skipWhitespace(json, index);
            if (index >= json.length()) {
                throw new IllegalArgumentException("Unclosed array");
            }
            if (json.charAt(index) == ']') {
                return index + 1;
            }
            if (json.charAt(index) != ',') {
                throw new IllegalArgumentException("Expected ',' or ']'");
            }
            index = skipWhitespace(json, index + 1);
        }
        throw new IllegalArgumentException("Unclosed array");
    }

    private static int parseString(String json, int start) {
        if (start >= json.length() || json.charAt(start) != '"') {
            throw new IllegalArgumentException("Expected string");
        }
        int index = start + 1;
        while (index < json.length()) {
            char current = json.charAt(index);
            if (current == '\\') {
                index += 2;
                continue;
            }
            if (current == '"') {
                return index + 1;
            }
            index++;
        }
        throw new IllegalArgumentException("Unclosed string");
    }

    private static int parseLiteral(String json, int start) {
        if (json.startsWith("true", start)) {
            return start + 4;
        }
        if (json.startsWith("false", start)) {
            return start + 5;
        }
        if (json.startsWith("null", start)) {
            return start + 4;
        }
        throw new IllegalArgumentException("Invalid literal");
    }

    private static int parseNumber(String json, int start) {
        int index = start;
        if (json.charAt(index) == '-') {
            index++;
        }
        if (index >= json.length() || !Character.isDigit(json.charAt(index))) {
            throw new IllegalArgumentException("Invalid number");
        }
        while (index < json.length() && Character.isDigit(json.charAt(index))) {
            index++;
        }
        if (index < json.length() && json.charAt(index) == '.') {
            index++;
            while (index < json.length() && Character.isDigit(json.charAt(index))) {
                index++;
            }
        }
        if (index < json.length() && (json.charAt(index) == 'e' || json.charAt(index) == 'E')) {
            index++;
            if (index < json.length() && (json.charAt(index) == '+' || json.charAt(index) == '-')) {
                index++;
            }
            while (index < json.length() && Character.isDigit(json.charAt(index))) {
                index++;
            }
        }
        return index;
    }

    private static int skipWhitespace(String json, int index) {
        while (index < json.length() && Character.isWhitespace(json.charAt(index))) {
            index++;
        }
        return index;
    }
}
