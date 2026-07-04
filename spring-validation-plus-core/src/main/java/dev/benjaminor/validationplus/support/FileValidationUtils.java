package dev.benjaminor.validationplus.support;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utilities for uploaded file and image validations.
 */
public final class FileValidationUtils {

    static final String[] DEFAULT_IMAGE_MIME_TYPES = {
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp",
            "image/bmp",
            "image/x-icon"
    };

    private FileValidationUtils() {
    }

    public record ValidationOutcome(boolean valid, String messageKey) {

        public static ValidationOutcome ok() {
            return new ValidationOutcome(true, null);
        }

        public static ValidationOutcome fail(String messageKey) {
            return new ValidationOutcome(false, messageKey);
        }
    }

    public static ValidationOutcome validateFile(
            Object value,
            int maxKilobytes,
            String[] mimeTypes,
            String[] extensions) {
        Optional<UploadHandle> upload = UploadUtils.resolve(value);
        if (upload.isEmpty()) {
            if (value == null) {
                return ValidationOutcome.ok();
            }
            return ValidationOutcome.fail("dev.benjaminor.validationplus.constraints.File.message");
        }

        UploadHandle handle = upload.get();
        if (handle.isEmpty()) {
            return ValidationOutcome.ok();
        }

        if (maxKilobytes > 0 && handle.getSize() > (long) maxKilobytes * 1024L) {
            return ValidationOutcome.fail("dev.benjaminor.validationplus.constraints.File.max.message");
        }

        if (mimeTypes.length > 0 && !matchesMime(handle, mimeTypes)) {
            return ValidationOutcome.fail("dev.benjaminor.validationplus.constraints.File.mime.message");
        }

        if (extensions.length > 0 && !matchesExtension(handle, extensions)) {
            return ValidationOutcome.fail("dev.benjaminor.validationplus.constraints.File.extension.message");
        }

        return ValidationOutcome.ok();
    }

    public static ValidationOutcome validateImage(
            Object value,
            int maxKilobytes,
            String[] mimeTypes,
            int minWidth,
            int maxWidth,
            int minHeight,
            int maxHeight) {
        String[] effectiveMimeTypes = mimeTypes.length > 0 ? mimeTypes : DEFAULT_IMAGE_MIME_TYPES;
        ValidationOutcome fileOutcome = validateFile(value, maxKilobytes, effectiveMimeTypes, new String[0]);
        if (!fileOutcome.valid()) {
            return mapImageFileFailure(fileOutcome.messageKey());
        }

        Optional<UploadHandle> upload = UploadUtils.resolve(value);
        if (upload.isEmpty() || upload.get().isEmpty()) {
            return ValidationOutcome.ok();
        }

        try (InputStream inputStream = upload.get().openStream()) {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                return ValidationOutcome.fail("dev.benjaminor.validationplus.constraints.Image.message");
            }
            if (minWidth > 0 && image.getWidth() < minWidth) {
                return ValidationOutcome.fail("dev.benjaminor.validationplus.constraints.Image.dimensions.message");
            }
            if (maxWidth > 0 && image.getWidth() > maxWidth) {
                return ValidationOutcome.fail("dev.benjaminor.validationplus.constraints.Image.dimensions.message");
            }
            if (minHeight > 0 && image.getHeight() < minHeight) {
                return ValidationOutcome.fail("dev.benjaminor.validationplus.constraints.Image.dimensions.message");
            }
            if (maxHeight > 0 && image.getHeight() > maxHeight) {
                return ValidationOutcome.fail("dev.benjaminor.validationplus.constraints.Image.dimensions.message");
            }
            return ValidationOutcome.ok();
        } catch (IOException exception) {
            return ValidationOutcome.fail("dev.benjaminor.validationplus.constraints.Image.message");
        }
    }

    private static ValidationOutcome mapImageFileFailure(String messageKey) {
        if ("dev.benjaminor.validationplus.constraints.File.message".equals(messageKey)) {
            return ValidationOutcome.fail("dev.benjaminor.validationplus.constraints.Image.message");
        }
        return ValidationOutcome.fail(messageKey);
    }

    public static boolean applyOutcome(ValidationOutcome outcome, jakarta.validation.ConstraintValidatorContext context) {
        if (outcome.valid()) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("{" + outcome.messageKey() + "}")
                .addConstraintViolation();
        return false;
    }

    static boolean matchesMime(UploadHandle handle, String[] mimeTypes) {
        Set<String> allowed = normalizeList(mimeTypes);
        String contentType = normalizeToken(handle.getContentType());
        if (!contentType.isBlank() && allowed.contains(contentType)) {
            return true;
        }
        String extension = extractExtension(handle.getOriginalFilename());
        return !extension.isBlank() && allowed.contains(extension);
    }

    static boolean matchesExtension(UploadHandle handle, String[] extensions) {
        Set<String> allowed = normalizeList(extensions);
        String extension = extractExtension(handle.getOriginalFilename());
        return !extension.isBlank() && allowed.contains(extension);
    }

    static Set<String> normalizeList(String[] values) {
        return Arrays.stream(values)
                .filter(value -> value != null && !value.isBlank())
                .map(FileValidationUtils::normalizeToken)
                .collect(Collectors.toSet());
    }

    static String normalizeToken(String value) {
        if (value == null) {
            return "";
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        if (normalized.startsWith(".")) {
            normalized = normalized.substring(1);
        }
        return normalized;
    }

    static String extractExtension(String filename) {
        if (filename == null || filename.isBlank()) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }
}
