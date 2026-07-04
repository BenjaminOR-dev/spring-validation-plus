package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.ActiveUrl;
import dev.benjaminor.validationplus.constraints.AlphaDash;
import dev.benjaminor.validationplus.constraints.AlphaNum;
import dev.benjaminor.validationplus.constraints.Ascii;
import dev.benjaminor.validationplus.constraints.Lowercase;
import dev.benjaminor.validationplus.constraints.MacAddress;
import dev.benjaminor.validationplus.constraints.Timezone;
import dev.benjaminor.validationplus.constraints.Uppercase;
import dev.benjaminor.validationplus.support.ValidatorTestSupport;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class RemainingValidatorsTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = ValidatorTestSupport.createValidator(Locale.forLanguageTag("es"));
    }

    @Test
    void alphaNumAndAlphaDashShouldValidateAlphanumericText() {
        TextDto dto = new TextDto();
        dto.alphaNum = "abc123";
        dto.alphaDash = "slug_name-1";

        assertThat(validator.validate(dto)).isEmpty();

        dto.alphaNum = "abc-123";
        dto.alphaDash = "invalid space";
        assertThat(validator.validateProperty(dto, "alphaNum")).isNotEmpty();
        assertThat(validator.validateProperty(dto, "alphaDash")).isNotEmpty();
    }

    @Test
    void asciiLowercaseAndUppercaseShouldValidateCharacterRules() {
        TextDto dto = new TextDto();
        dto.ascii = "Hello-123";
        dto.lowercase = "hello";
        dto.uppercase = "HELLO";

        assertThat(validator.validate(dto)).isEmpty();

        dto.ascii = "café";
        dto.lowercase = "Hello";
        dto.uppercase = "Hello";
        assertThat(validator.validateProperty(dto, "ascii")).isNotEmpty();
        assertThat(validator.validateProperty(dto, "lowercase")).isNotEmpty();
        assertThat(validator.validateProperty(dto, "uppercase")).isNotEmpty();
    }

    @Test
    void macAddressShouldValidateHardwareFormat() {
        NetworkDto dto = new NetworkDto();
        dto.macAddress = "00:1A:2B:3C:4D:5E";

        assertThat(validator.validateProperty(dto, "macAddress")).isEmpty();

        dto.macAddress = "not-a-mac";
        assertThat(validator.validateProperty(dto, "macAddress")).isNotEmpty();
    }

    @Test
    void activeUrlShouldValidateHttpUrlsWithHost() {
        NetworkDto dto = new NetworkDto();
        dto.activeUrl = "https://example.com/path";

        assertThat(validator.validateProperty(dto, "activeUrl")).isEmpty();

        dto.activeUrl = "ftp://example.com";
        assertThat(validator.validateProperty(dto, "activeUrl")).isNotEmpty();
    }

    @Test
    void timezoneShouldValidateZoneIds() {
        NetworkDto dto = new NetworkDto();
        dto.timezone = "America/Mexico_City";

        assertThat(validator.validateProperty(dto, "timezone")).isEmpty();

        dto.timezone = "Invalid/Timezone";
        assertThat(validator.validateProperty(dto, "timezone")).isNotEmpty();
    }

    static class TextDto {
        @AlphaNum
        private String alphaNum;

        @AlphaDash
        private String alphaDash;

        @Ascii
        private String ascii;

        @Lowercase
        private String lowercase;

        @Uppercase
        private String uppercase;
    }

    static class NetworkDto {
        @MacAddress
        private String macAddress;

        @ActiveUrl
        private String activeUrl;

        @Timezone
        private String timezone;
    }
}
