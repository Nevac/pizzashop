package ch.cagatay.pizzashop.dto;

import ch.cagatay.pizzashop.model.OrderStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class OrderDtoPost {
    @JsonProperty("id")
    Long id;

    @JsonProperty("address")
    String address;

    @JsonProperty("phone")
    String phone;

    @JsonProperty("price")
    float price;

    @JsonProperty("status")
    OrderStatus status;

    @JsonProperty("pizzas")
    List<Long> pizzaIds;
}
