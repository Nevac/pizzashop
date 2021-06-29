package ch.cagatay.pizzashop.dto;

import ch.cagatay.pizzashop.model.OrderStatus;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@JsonAutoDetect
@Data
public class OrderDtoIn {
    @NotNull
    @JsonProperty("address")
    String address;

    @NotNull
    @JsonProperty("phone")
    String phone;

    @NotNull
    @JsonProperty("status")
    OrderStatus status;

    @NotNull
    @JsonProperty("pizzas")
    List<Long> pizzaIds;
}
