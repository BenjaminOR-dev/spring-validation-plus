package dev.benjaminor.validationplus.exception;

import dev.benjaminor.validationplus.support.ValidationMessageUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Builds ordered validation error responses from Spring binding results.
 */
public final class ValidationErrorResponseFactory {

    private ValidationErrorResponseFactory() {
    }

    public static ValidationErrorResponse fromBindingResult(BindingResult bindingResult) {
        Locale locale = LocaleContextHolder.getLocale();
        Class<?> targetType = bindingResult.getTarget() != null
                ? bindingResult.getTarget().getClass()
                : null;
        List<ValidationErrorOrdering.ValidationErrorEntry> entries = new ArrayList<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            entries.add(new ValidationErrorOrdering.ValidationErrorEntry(
                    fieldError.getField(),
                    FieldErrorMessageResolver.resolve(fieldError, locale),
                    ValidationErrorOrdering.extractConstraintName(fieldError)));
        }
        return new ValidationErrorResponse(ValidationErrorOrdering.order(targetType, entries));
    }

    public static ValidationErrorResponse fromMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        return fromBindingResult(exception.getBindingResult());
    }

    public static ValidationErrorResponse fromBindException(BindException exception) {
        return fromBindingResult(exception.getBindingResult());
    }

    public static ValidationErrorResponse fromHandlerMethodValidation(HandlerMethodValidationExceptionData data) {
        Locale locale = LocaleContextHolder.getLocale();
        List<ValidationErrorOrdering.ValidationErrorEntry> entries = new ArrayList<>();
        Class<?> targetType = null;
        for (ParameterValidationResult result : data.validationResults()) {
            String field = data.parameterNameResolver().apply(result);
            if (targetType == null) {
                targetType = result.getMethodParameter().getParameterType();
            }
            for (var resolvableError : result.getResolvableErrors()) {
                String message = data.resolvableErrorMessageResolver().resolve(resolvableError, locale, field);
                if (message != null && !message.isBlank()) {
                    String constraintName = resolvableError instanceof FieldError fieldError
                            ? ValidationErrorOrdering.extractConstraintName(fieldError)
                            : "";
                    entries.add(new ValidationErrorOrdering.ValidationErrorEntry(field, message, constraintName));
                }
            }
        }
        Map<String, List<String>> errors = ValidationErrorOrdering.order(targetType, entries);
        if (errors.isEmpty()) {
            errors = ValidationErrorOrdering.order(
                    null,
                    List.of(new ValidationErrorOrdering.ValidationErrorEntry(
                            "request",
                            ValidationMessageUtils.resolve(
                                    "dev.benjaminor.validationplus.type.generic",
                                    locale,
                                    Map.of("field", "request")),
                            "")));
        }
        return new ValidationErrorResponse(errors);
    }

    public static ValidationErrorResponse fromConstraintViolations(ConstraintViolationException exception) {
        List<ValidationErrorOrdering.ValidationErrorEntry> entries = new ArrayList<>();
        Class<?> targetType = null;
        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            if (targetType == null) {
                targetType = violation.getRootBeanClass();
            }
            entries.add(new ValidationErrorOrdering.ValidationErrorEntry(
                    normalizePropertyPath(violation.getPropertyPath().toString()),
                    violation.getMessage(),
                    ValidationErrorOrdering.extractConstraintName(violation)));
        }
        return new ValidationErrorResponse(ValidationErrorOrdering.order(targetType, entries));
    }

    private static String normalizePropertyPath(String propertyPath) {
        if (propertyPath == null || propertyPath.isBlank()) {
            return "request";
        }
        int lastDot = propertyPath.lastIndexOf('.');
        if (lastDot >= 0 && lastDot < propertyPath.length() - 1) {
            return propertyPath.substring(lastDot + 1);
        }
        return propertyPath;
    }

    @FunctionalInterface
    public interface ParameterNameResolver {
        String apply(ParameterValidationResult result);
    }

    @FunctionalInterface
    public interface ResolvableErrorMessageResolver {
        String resolve(
                org.springframework.context.MessageSourceResolvable resolvableError,
                Locale locale,
                String field);
    }

    public record HandlerMethodValidationExceptionData(
            List<ParameterValidationResult> validationResults,
            ParameterNameResolver parameterNameResolver,
            ResolvableErrorMessageResolver resolvableErrorMessageResolver) {
    }
}
