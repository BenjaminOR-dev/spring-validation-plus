package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.File;
import dev.benjaminor.validationplus.constraints.Image;
import dev.benjaminor.validationplus.support.UploadHandle;
import dev.benjaminor.validationplus.support.ValidatorTestSupport;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class FileValidatorsTest {

    private static final byte[] ONE_BY_ONE_PNG = Base64.getDecoder().decode(
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg==");

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = ValidatorTestSupport.createValidator(Locale.forLanguageTag("es"));
    }

    @Test
    void fileShouldAcceptValidUpload() {
        FileDto dto = new FileDto();
        dto.document = new TestUpload(ONE_BY_ONE_PNG, "application/pdf", "report.pdf");

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void fileShouldRejectNonUploadValues() {
        FileDto dto = new FileDto();
        dto.document = "not-a-file";

        assertThat(validator.validate(dto)).isNotEmpty();
    }

    @Test
    void fileShouldRejectOversizedUpload() {
        FileDto dto = new FileDto();
        dto.smallDocument = new TestUpload(new byte[2048], "application/pdf", "large.pdf");

        assertThat(validator.validateProperty(dto, "smallDocument")).isNotEmpty();
    }

    @Test
    void fileShouldValidateMimeTypesAndExtensions() {
        FileDto dto = new FileDto();
        dto.typedDocument = new TestUpload(ONE_BY_ONE_PNG, "application/pdf", "report.pdf");
        dto.extensionDocument = new TestUpload(ONE_BY_ONE_PNG, "application/pdf", "report.pdf");

        assertThat(validator.validateProperty(dto, "typedDocument")).isEmpty();
        assertThat(validator.validateProperty(dto, "extensionDocument")).isEmpty();

        dto.typedDocument = new TestUpload(ONE_BY_ONE_PNG, "text/plain", "notes.txt");
        dto.extensionDocument = new TestUpload(ONE_BY_ONE_PNG, "application/pdf", "report.doc");

        assertThat(validator.validateProperty(dto, "typedDocument")).isNotEmpty();
        assertThat(validator.validateProperty(dto, "extensionDocument")).isNotEmpty();
    }

    @Test
    void imageShouldValidateDimensions() {
        FileDto dto = new FileDto();
        dto.avatar = new TestUpload(ONE_BY_ONE_PNG, "image/png", "avatar.png");

        assertThat(validator.validateProperty(dto, "avatar")).isEmpty();

        dto.avatar = new TestUpload(ONE_BY_ONE_PNG, "application/pdf", "avatar.pdf");

        assertThat(validator.validateProperty(dto, "avatar")).isNotEmpty();
    }

    @Test
    void nullAndEmptyUploadsShouldPass() {
        FileDto dto = new FileDto();
        dto.document = null;

        assertThat(validator.validate(dto)).isEmpty();

        dto.document = TestUpload.empty();
        assertThat(validator.validate(dto)).isEmpty();
    }

    static class FileDto {

        @File
        Object document;

        @File(max = 1)
        Object smallDocument;

        @File(mimeTypes = {"application/pdf", "pdf"})
        Object typedDocument;

        @File(extensions = {"pdf"})
        Object extensionDocument;

        @Image(minWidth = 1, minHeight = 1, maxWidth = 10, maxHeight = 10)
        Object avatar;
    }

    static final class TestUpload implements UploadHandle {

        private final byte[] content;
        private final String contentType;
        private final String filename;
        private final boolean empty;

        TestUpload(byte[] content, String contentType, String filename) {
            this(content, contentType, filename, false);
        }

        private TestUpload(byte[] content, String contentType, String filename, boolean empty) {
            this.content = content != null ? content : new byte[0];
            this.contentType = contentType;
            this.filename = filename;
            this.empty = empty;
        }

        static TestUpload empty() {
            return new TestUpload(new byte[0], null, null, true);
        }

        @Override
        public boolean isEmpty() {
            return empty;
        }

        @Override
        public long getSize() {
            return content.length;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public String getOriginalFilename() {
            return filename;
        }

        @Override
        public InputStream openStream() throws IOException {
            return new ByteArrayInputStream(content);
        }
    }
}
