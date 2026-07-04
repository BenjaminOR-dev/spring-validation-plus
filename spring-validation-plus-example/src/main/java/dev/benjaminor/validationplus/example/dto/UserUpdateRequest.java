package dev.benjaminor.validationplus.example.dto;

import dev.benjaminor.validationplus.example.entity.User;
import dev.benjaminor.validationplus.constraints.EmailAddress;
import dev.benjaminor.validationplus.constraints.MaxLength;
import dev.benjaminor.validationplus.constraints.MinLength;
import dev.benjaminor.validationplus.constraints.Nullable;
import dev.benjaminor.validationplus.constraints.Required;
import dev.benjaminor.validationplus.constraints.StringType;
import dev.benjaminor.validationplus.constraints.Unique;

/**
 * Patrón: PUT body + {@code excludeParameter} para ignorar el registro actual.
 * Password opcional: omitir el campo o enviar {@code null} (no {@code ""}).
 */
@Unique(
        entity = User.class,
        field = "email",
        column = "email",
        excludeParameter = "id",
        message = "El email ya está registrado por otro usuario."
)
public class UserUpdateRequest {

    @Required
    @StringType
    @MinLength(2)
    @MaxLength(50)
    private String name;

    @Required
    @StringType
    @EmailAddress
    @MaxLength(255)
    private String email;

    @Nullable
    @StringType
    @MinLength(6)
    @MaxLength(255)
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
