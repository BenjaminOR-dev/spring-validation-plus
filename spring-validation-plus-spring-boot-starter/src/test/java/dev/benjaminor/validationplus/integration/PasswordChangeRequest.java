package dev.benjaminor.validationplus.integration;

import dev.benjaminor.validationplus.constraints.Confirmed;
import dev.benjaminor.validationplus.constraints.MinLength;
import dev.benjaminor.validationplus.constraints.Nullable;
import dev.benjaminor.validationplus.constraints.RequiredWith;
import dev.benjaminor.validationplus.constraints.StringType;

public class PasswordChangeRequest {

    @Nullable
    @StringType
    @MinLength(6)
    private String password;

    @RequiredWith("password")
    @Confirmed("password")
    @StringType
    @MinLength(5)
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
