package dev.benjaminor.validationplus.exception;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintViolation;
import org.springframework.validation.FieldError;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Orders validation errors according to field and annotation declaration order on the target type.
 */
public final class ValidationErrorOrdering {

    private static final int UNKNOWN_ORDER = Integer.MAX_VALUE;

    private ValidationErrorOrdering() {
    }

    public record ValidationErrorEntry(String field, String message, String constraintName) {
    }

    public static Map<String, List<String>> order(Class<?> targetType, List<ValidationErrorEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            return Map.of();
        }
        if (targetType == null) {
            return toUnorderedMap(entries);
        }

        Map<String, Integer> fieldOrder = buildFieldOrder(targetType);
        Map<String, Map<String, Integer>> constraintOrderByField = buildConstraintOrderByField(targetType);

        List<IndexedEntry> indexedEntries = new ArrayList<>(entries.size());
        for (int index = 0; index < entries.size(); index++) {
            indexedEntries.add(new IndexedEntry(index, entries.get(index)));
        }
        indexedEntries.sort(Comparator
                .comparingInt((IndexedEntry indexedEntry) ->
                        fieldOrder.getOrDefault(indexedEntry.entry().field(), UNKNOWN_ORDER))
                .thenComparingInt(indexedEntry ->
                        constraintOrderFor(indexedEntry.entry(), constraintOrderByField))
                .thenComparingInt(IndexedEntry::index));

        List<ValidationErrorEntry> sortedEntries = indexedEntries.stream()
                .map(IndexedEntry::entry)
                .toList();

        return toOrderedMap(sortedEntries);
    }

    public static String extractConstraintName(FieldError error) {
        if (error == null) {
            return "";
        }
        String constraintName = extractConstraintNameFromCode(error.getCode());
        if (!constraintName.isBlank()) {
            return constraintName;
        }
        String[] codes = error.getCodes();
        if (codes != null) {
            for (String code : codes) {
                constraintName = extractConstraintNameFromCode(code);
                if (!constraintName.isBlank()) {
                    return constraintName;
                }
            }
        }
        return "";
    }

    public static String extractConstraintName(ConstraintViolation<?> violation) {
        if (violation == null || violation.getConstraintDescriptor() == null) {
            return "";
        }
        Annotation annotation = violation.getConstraintDescriptor().getAnnotation();
        return annotation != null ? annotation.annotationType().getSimpleName() : "";
    }

    private static int constraintOrderFor(
            ValidationErrorEntry entry,
            Map<String, Map<String, Integer>> constraintOrderByField) {
        Map<String, Integer> constraintOrder = constraintOrderByField.get(entry.field());
        if (constraintOrder == null) {
            return UNKNOWN_ORDER;
        }
        return constraintOrder.getOrDefault(entry.constraintName(), UNKNOWN_ORDER);
    }

    private static String extractConstraintNameFromCode(String code) {
        if (code == null || code.isBlank()) {
            return "";
        }
        int dotIndex = code.indexOf('.');
        if (dotIndex <= 0) {
            return code;
        }
        return code.substring(0, dotIndex);
    }

    private static Map<String, Integer> buildFieldOrder(Class<?> targetType) {
        Map<String, Integer> fieldOrder = new HashMap<>();
        int index = 0;
        for (Field field : declaredFieldsInHierarchy(targetType)) {
            fieldOrder.putIfAbsent(field.getName(), index++);
        }
        return fieldOrder;
    }

    private static Map<String, Map<String, Integer>> buildConstraintOrderByField(Class<?> targetType) {
        Map<String, Map<String, Integer>> constraintOrderByField = new HashMap<>();
        for (Field field : declaredFieldsInHierarchy(targetType)) {
            Map<String, Integer> constraintOrder = new HashMap<>();
            Annotation[] annotations = field.getDeclaredAnnotations();
            int annotationIndex = 0;
            for (Annotation annotation : annotations) {
                if (isValidationConstraint(annotation)) {
                    constraintOrder.putIfAbsent(annotation.annotationType().getSimpleName(), annotationIndex++);
                }
            }
            constraintOrderByField.put(field.getName(), constraintOrder);
        }
        return constraintOrderByField;
    }

    private static List<Field> declaredFieldsInHierarchy(Class<?> targetType) {
        List<Field> fields = new ArrayList<>();
        Class<?> current = targetType;
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                fields.add(field);
            }
            current = current.getSuperclass();
        }
        return fields;
    }

    private static boolean isValidationConstraint(Annotation annotation) {
        return annotation.annotationType().isAnnotationPresent(Constraint.class);
    }

    private static Map<String, List<String>> toOrderedMap(List<ValidationErrorEntry> entries) {
        Map<String, List<String>> errors = new LinkedHashMap<>();
        for (ValidationErrorEntry entry : entries) {
            errors.computeIfAbsent(entry.field(), ignored -> new ArrayList<>()).add(entry.message());
        }
        return errors;
    }

    private static Map<String, List<String>> toUnorderedMap(List<ValidationErrorEntry> entries) {
        return toOrderedMap(entries);
    }

    private record IndexedEntry(int index, ValidationErrorEntry entry) {
    }
}
