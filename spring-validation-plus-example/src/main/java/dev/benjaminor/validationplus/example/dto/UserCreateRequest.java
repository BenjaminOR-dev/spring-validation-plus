package dev.benjaminor.validationplus.example.dto;

import dev.benjaminor.validationplus.example.entity.User;
import dev.benjaminor.validationplus.constraints.EmailAddress;
import dev.benjaminor.validationplus.constraints.MaxLength;
import dev.benjaminor.validationplus.constraints.MinLength;
import dev.benjaminor.validationplus.constraints.Required;
import dev.benjaminor.validationplus.constraints.StringType;
import dev.benjaminor.validationplus.constraints.Unique;

/**
 * Pattern: POST body + {@code @Unique} on create.
 */
@Unique(entity = User.class, field = "email", column = "email")
public class UserCreateRequest {

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
}
