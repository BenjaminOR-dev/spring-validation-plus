package dev.benjaminor.validationplus.support;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Utilidades para validaciones de fechas.
 */
public final class DateValidationUtils {

    private DateValidationUtils() {
    }

    public static boolean isDate(Object value, String format) {
        if (value == null) {
            return true;
        }
        if (isTemporalType(value)) {
            return true;
        }
        if (value instanceof CharSequence charSequence) {
            return parseToInstant(charSequence.toString().trim(), format) != null;
        }
        return false;
    }

    public static boolean isBefore(Object value, Object compareTo, String format) {
        if (value == null) {
            return true;
        }
        Instant left = toInstant(value, format);
        Instant right = toInstant(compareTo, format);
        if (left == null || right == null) {
            return false;
        }
        return left.isBefore(right);
    }

    public static boolean isAfter(Object value, Object compareTo, String format) {
        if (value == null) {
            return true;
        }
        Instant left = toInstant(value, format);
        Instant right = toInstant(compareTo, format);
        if (left == null || right == null) {
            return false;
        }
        return left.isAfter(right);
    }

    public static boolean isBeforeOrEqual(Object value, Object compareTo, String format) {
        if (value == null) {
            return true;
        }
        Instant left = toInstant(value, format);
        Instant right = toInstant(compareTo, format);
        if (left == null || right == null) {
            return false;
        }
        return !left.isAfter(right);
    }

    public static boolean isAfterOrEqual(Object value, Object compareTo, String format) {
        if (value == null) {
            return true;
        }
        Instant left = toInstant(value, format);
        Instant right = toInstant(compareTo, format);
        if (left == null || right == null) {
            return false;
        }
        return !left.isBefore(right);
    }

    public static boolean isTimezone(Object value) {
        if (EmptyUtils.isBlank(value)) {
            return true;
        }
        if (!(value instanceof CharSequence charSequence)) {
            return false;
        }
        try {
            java.time.ZoneId.of(charSequence.toString().trim());
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public static boolean isDateFormat(Object value, String format) {
        if (format == null || format.isBlank()) {
            return false;
        }
        return isDate(value, format);
    }

    public static Instant toInstant(Object value, String format) {
        if (value == null) {
            return null;
        }
        if (value instanceof Instant instant) {
            return instant;
        }
        if (value instanceof LocalDate localDate) {
            return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        }
        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        }
        if (value instanceof OffsetDateTime offsetDateTime) {
            return offsetDateTime.toInstant();
        }
        if (value instanceof ZonedDateTime zonedDateTime) {
            return zonedDateTime.toInstant();
        }
        if (value instanceof Date date) {
            return date.toInstant();
        }
        if (value instanceof Calendar calendar) {
            return calendar.toInstant();
        }
        if (value instanceof CharSequence charSequence) {
            return parseToInstant(charSequence.toString().trim(), format);
        }
        return null;
    }

    private static boolean isTemporalType(Object value) {
        return value instanceof LocalDate
                || value instanceof LocalDateTime
                || value instanceof OffsetDateTime
                || value instanceof ZonedDateTime
                || value instanceof Instant
                || value instanceof Date
                || value instanceof Calendar;
    }

    private static Instant parseToInstant(String value, String format) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            if (format != null && !format.isBlank()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                try {
                    return LocalDateTime.parse(value, formatter).atZone(ZoneId.systemDefault()).toInstant();
                } catch (DateTimeParseException ignored) {
                    return LocalDate.parse(value, formatter).atStartOfDay(ZoneId.systemDefault()).toInstant();
                }
            }
            try {
                return Instant.parse(value);
            } catch (DateTimeParseException ignored) {
                // Continue with local formats.
            }
            try {
                return LocalDateTime.parse(value).atZone(ZoneId.systemDefault()).toInstant();
            } catch (DateTimeParseException ignored) {
                return LocalDate.parse(value).atStartOfDay(ZoneId.systemDefault()).toInstant();
            }
        } catch (DateTimeParseException exception) {
            return null;
        }
    }
}
