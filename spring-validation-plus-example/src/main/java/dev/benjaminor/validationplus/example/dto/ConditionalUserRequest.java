package dev.benjaminor.validationplus.example.dto;

import dev.benjaminor.validationplus.constraints.BooleanType;
import dev.benjaminor.validationplus.constraints.DecimalType;
import dev.benjaminor.validationplus.constraints.EmailAddress;
import dev.benjaminor.validationplus.constraints.IntegerType;
import dev.benjaminor.validationplus.constraints.MaxLength;
import dev.benjaminor.validationplus.constraints.MinLength;
import dev.benjaminor.validationplus.constraints.MinValue;
import dev.benjaminor.validationplus.constraints.Nullable;
import dev.benjaminor.validationplus.constraints.Required;
import dev.benjaminor.validationplus.constraints.RequiredIf;
import dev.benjaminor.validationplus.constraints.StringType;

/**
 * Patrón: tipos Java + regla condicional {@code @RequiredIf} a nivel clase.
 */
@RequiredIf(field = "role", value = "ADMIN", required = "adminCode")
public class ConditionalUserRequest {

    @Required
    @StringType
    @MinLength(2)
    @MaxLength(50)
    private String name;

    @Required
    @EmailAddress
    private String email;

    @Required
    @IntegerType
    @MinValue(1)
    private Integer size;

    @Nullable
    @BooleanType
    private Boolean active;

    @Nullable
    @DecimalType
    private Double score;

    private String role;

    private String adminCode;

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

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAdminCode() {
        return adminCode;
    }

    public void setAdminCode(String adminCode) {
        this.adminCode = adminCode;
    }
}
