package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.File;
import dev.benjaminor.validationplus.support.FileValidationUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator para {@link File}.
 */
public class FileValidator implements ConstraintValidator<File, Object> {

    private int maxKilobytes;
    private String[] mimeTypes;
    private String[] extensions;

    @Override
    public void initialize(File constraintAnnotation) {
        this.maxKilobytes = constraintAnnotation.max();
        this.mimeTypes = constraintAnnotation.mimeTypes();
        this.extensions = constraintAnnotation.extensions();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        FileValidationUtils.ValidationOutcome outcome =
                FileValidationUtils.validateFile(value, maxKilobytes, mimeTypes, extensions);
        return FileValidationUtils.applyOutcome(outcome, context);
    }
}
