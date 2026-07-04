package dev.benjaminor.validationplus.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import dev.benjaminor.validationplus.support.ValidationMessageUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Transforma errores de validación y conversión de Spring en respuestas amigables.
 */
@RestControllerAdvice
public class ValidationExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ValidationErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        Map<String, List<String>> errors = new LinkedHashMap<>();
        Locale locale = LocaleContextHolder.getLocale();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            addError(errors, fieldError.getField(), FieldErrorMessageResolver.resolve(fieldError, locale));
        }
        return new ValidationErrorResponse(errors);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ValidationErrorResponse handleBindException(BindException exception) {
        Map<String, List<String>> errors = new LinkedHashMap<>();
        Locale locale = LocaleContextHolder.getLocale();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            addError(errors, fieldError.getField(), FieldErrorMessageResolver.resolve(fieldError, locale));
        }
        return new ValidationErrorResponse(errors);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ValidationErrorResponse handleConstraintViolation(ConstraintViolationException exception) {
        Map<String, List<String>> errors = new LinkedHashMap<>();
        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            addError(errors, violation.getPropertyPath().toString(), violation.getMessage());
        }
        return new ValidationErrorResponse(errors);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ValidationErrorResponse handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
        Map<String, List<String>> errors = new LinkedHashMap<>();
        String field = exception.getName();
        String message = resolveTypeMismatchMessage(field, exception.getRequiredType());
        addError(errors, field, message);
        return new ValidationErrorResponse(errors);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            ConversionFailedException.class,
            org.springframework.beans.TypeMismatchException.class
    })
    public ValidationErrorResponse handleConversionException(Exception exception) {
        Map<String, List<String>> errors = new LinkedHashMap<>();
        String field = extractFieldName(exception);
        Class<?> targetType = extractTargetType(exception);
        addError(errors, field, resolveTypeMismatchMessage(field, targetType));
        return new ValidationErrorResponse(errors);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ValidationErrorResponse handleNotReadable(HttpMessageNotReadableException exception) {
        Map<String, List<String>> errors = new LinkedHashMap<>();
        Throwable cause = exception.getMostSpecificCause();

        if (cause instanceof InvalidFormatException invalidFormatException) {
            String field = extractJacksonField(invalidFormatException);
            addError(errors, field, resolveTypeMismatchMessage(field, invalidFormatException.getTargetType()));
        } else if (cause instanceof MismatchedInputException mismatchedInputException) {
            String field = extractJacksonField(mismatchedInputException);
            Class<?> targetType = mismatchedInputException.getTargetType();
            addError(errors, field, resolveTypeMismatchMessage(field, targetType));
        } else if (cause instanceof JsonParseException) {
            addError(errors, "body", resolveMalformedJsonMessage());
        } else {
            String field = extractJsonField(exception);
            addError(errors, field, resolveTypeMismatchMessage(field, null));
        }
        return new ValidationErrorResponse(errors);
    }

    private String resolveTypeMismatchMessage(String field, Class<?> targetType) {
        Locale locale = LocaleContextHolder.getLocale();
        String messageKey = resolveMessageKey(targetType);
        return ValidationMessageUtils.resolve(messageKey, locale, Map.of("field", field));
    }

    private String resolveMalformedJsonMessage() {
        Locale locale = LocaleContextHolder.getLocale();
        return ValidationMessageUtils.resolve("dev.benjaminor.validationplus.type.malformed", locale, Map.of());
    }

    private String resolveMessageKey(Class<?> targetType) {
        if (targetType == null) {
            return "dev.benjaminor.validationplus.type.generic";
        }
        if (Integer.class.equals(targetType) || int.class.equals(targetType)
                || Long.class.equals(targetType) || long.class.equals(targetType)
                || Short.class.equals(targetType) || short.class.equals(targetType)
                || Byte.class.equals(targetType) || byte.class.equals(targetType)) {
            return "dev.benjaminor.validationplus.type.integer";
        }
        if (Float.class.equals(targetType) || float.class.equals(targetType)
                || Double.class.equals(targetType) || double.class.equals(targetType)
                || java.math.BigDecimal.class.equals(targetType)) {
            return "dev.benjaminor.validationplus.type.decimal";
        }
        if (Boolean.class.equals(targetType) || boolean.class.equals(targetType)) {
            return "dev.benjaminor.validationplus.type.boolean";
        }
        if (CharSequence.class.isAssignableFrom(targetType) || String.class.equals(targetType)) {
            return "dev.benjaminor.validationplus.type.string";
        }
        if (targetType.isArray() || Collection.class.isAssignableFrom(targetType)) {
            return "dev.benjaminor.validationplus.type.array";
        }
        return "dev.benjaminor.validationplus.type.generic";
    }

    private String extractFieldName(Exception exception) {
        if (exception instanceof ConversionFailedException conversionFailedException
                && conversionFailedException.getSourceType() != null) {
            return "value";
        }
        return "value";
    }

    private Class<?> extractTargetType(Exception exception) {
        if (exception instanceof ConversionFailedException conversionFailedException) {
            return conversionFailedException.getTargetType().getType();
        }
        if (exception instanceof org.springframework.beans.TypeMismatchException typeMismatchException) {
            return typeMismatchException.getRequiredType();
        }
        return null;
    }

    private String extractJacksonField(JsonMappingException exception) {
        if (exception.getPath() != null && !exception.getPath().isEmpty()) {
            JsonMappingException.Reference lastReference = exception.getPath().get(exception.getPath().size() - 1);
            if (lastReference.getFieldName() != null) {
                return lastReference.getFieldName();
            }
            if (lastReference.getIndex() >= 0) {
                return String.valueOf(lastReference.getIndex());
            }
        }
        return "body";
    }

    private String extractJsonField(HttpMessageNotReadableException exception) {
        String message = exception.getMostSpecificCause().getMessage();
        if (message != null) {
            int referenceIndex = message.indexOf("reference \"");
            if (referenceIndex >= 0) {
                int start = referenceIndex + "reference \"".length();
                int end = message.indexOf('"', start);
                if (end > start) {
                    return message.substring(start, end);
                }
            }
        }
        return "body";
    }

    private void addError(Map<String, List<String>> errors, String field, String message) {
        errors.computeIfAbsent(field, ignored -> new ArrayList<>()).add(message);
    }
}
