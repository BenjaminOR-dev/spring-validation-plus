package dev.benjaminor.validationplus.example.dto;

import dev.benjaminor.validationplus.constraints.Required;
import dev.benjaminor.validationplus.constraints.Same;
import dev.benjaminor.validationplus.constraints.StringType;

/**
 * Pattern: {@code @Same} — passwordConfirmation must match password.
 */
@Same(field = "password", other = "passwordConfirmation")
public class PasswordRequest {

    @Required
    @StringType
    private String password;

    private String passwordConfirmation;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }
}
