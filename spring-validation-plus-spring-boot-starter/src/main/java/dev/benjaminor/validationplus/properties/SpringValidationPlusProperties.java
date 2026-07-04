package dev.benjaminor.validationplus.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propiedades de configuración de Spring Validation Plus.
 */
@ConfigurationProperties(prefix = "spring.validation-plus")
public class SpringValidationPlusProperties {

    /**
     * Habilita o deshabilita la integración de Spring Validation Plus.
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
         * Habilita el {@code ControllerAdvice} para errores de validación y conversión.
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
