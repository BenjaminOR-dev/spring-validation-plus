package dev.benjaminor.validationplus.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Spring Validation Plus.
 */
@ConfigurationProperties(prefix = "spring.validation-plus")
public class SpringValidationPlusProperties {

    /**
     * Enables or disables the Spring Validation Plus integration.
     */
    private boolean enabled = true;

    private ExceptionHandler exceptionHandler = new ExceptionHandler();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public static class ExceptionHandler {

        /**
         * Enables the {@code ControllerAdvice} for validation and conversion errors.
         */
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
