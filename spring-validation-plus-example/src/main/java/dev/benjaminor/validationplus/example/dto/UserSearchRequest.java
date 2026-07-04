package dev.benjaminor.validationplus.example.dto;

import dev.benjaminor.validationplus.constraints.EmailAddress;
import dev.benjaminor.validationplus.constraints.IntegerType;
import dev.benjaminor.validationplus.constraints.MaxLength;
import dev.benjaminor.validationplus.constraints.MaxValue;
import dev.benjaminor.validationplus.constraints.MinValue;
import dev.benjaminor.validationplus.constraints.Nullable;
import dev.benjaminor.validationplus.constraints.StringType;

/**
 * Patrón: GET query params con {@code @Valid @ModelAttribute}.
 */
public class UserSearchRequest {

    @Nullable
    @StringType
    @MaxLength(50)
    private String name;

    @Nullable
    @StringType
    @EmailAddress
    @MaxLength(255)
    private String email;

    @IntegerType
    @MinValue(0)
    @MaxValue(10_000)
    private Integer page = 0;

    @IntegerType
    @MinValue(1)
    @MaxValue(100)
    private Integer size = 10;

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

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
