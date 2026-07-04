package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Image;
import dev.benjaminor.validationplus.support.FileValidationUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for {@link Image}.
 */
public class ImageValidator implements ConstraintValidator<Image, Object> {

    private int maxKilobytes;
    private String[] mimeTypes;
    private int minWidth;
    private int maxWidth;
    private int minHeight;
    private int maxHeight;

    @Override
    public void initialize(Image constraintAnnotation) {
        this.maxKilobytes = constraintAnnotation.max();
        this.mimeTypes = constraintAnnotation.mimeTypes();
        this.minWidth = constraintAnnotation.minWidth();
        this.maxWidth = constraintAnnotation.maxWidth();
        this.minHeight = constraintAnnotation.minHeight();
        this.maxHeight = constraintAnnotation.maxHeight();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        FileValidationUtils.ValidationOutcome outcome = FileValidationUtils.validateImage(
                value, maxKilobytes, mimeTypes, minWidth, maxWidth, minHeight, maxHeight);
        return FileValidationUtils.applyOutcome(outcome, context);
    }
}
