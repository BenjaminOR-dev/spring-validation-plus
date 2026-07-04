package dev.benjaminor.validationplus.example.dto;

import dev.benjaminor.validationplus.constraints.ArrayType;
import dev.benjaminor.validationplus.constraints.Between;
import dev.benjaminor.validationplus.constraints.Distinct;
import dev.benjaminor.validationplus.constraints.Required;
import dev.benjaminor.validationplus.constraints.StringType;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Patrón: array/colección + validación anidada con {@code @Valid}.
 */
public class OrderCreateRequest {

    @Required
    @StringType
    private String customerName;

    @Required
    @ArrayType
    @Between(min = 1, max = 20)
    @Distinct
    @Valid
    private List<OrderItemRequest> items;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
}
