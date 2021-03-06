package ch.cagatay.pizzashop.dto;

import ch.cagatay.pizzashop.model.OrderStatus;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@JsonAutoDetect
@Data
public class OrderDtoOut {
    @JsonProperty("id")
    Long id;

    @JsonProperty("address")
    String address;

    @JsonProperty("phone")
    String phone;

    @JsonProperty("status")
    OrderStatus status;

    @JsonProperty("pizzas")
    List<PizzaDto> pizzas;
}
