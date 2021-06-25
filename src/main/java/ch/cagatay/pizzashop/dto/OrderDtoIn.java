package ch.cagatay.pizzashop.dto;

import ch.cagatay.pizzashop.model.OrderStatus;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonAutoDetect
@Data
public class OrderDtoIn {
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
