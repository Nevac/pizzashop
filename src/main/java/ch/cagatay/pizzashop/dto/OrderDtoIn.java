package ch.cagatay.pizzashop.dto;

import ch.cagatay.pizzashop.model.OrderStatus;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@JsonAutoDetect
@Data
public class OrderDtoIn {
    @NotNull
    @NotBlank
    @JsonProperty("address")
    String address;

    @NotNull
    @NotBlank
    @JsonProperty("phone")
    String phone;

    @NotNull
    @NotBlank
    @JsonProperty("status")
    OrderStatus status;

    @NotNull
    @NotEmpty
    @JsonProperty("pizzas")
    List<Long> pizzaIds;
}
