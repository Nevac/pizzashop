package ch.cagatay.pizzashop.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@JsonAutoDetect
@Data
public class PizzaDto {
    @JsonProperty("id")
    Long id;

    @NotNull
    @NotBlank
    @JsonProperty("name")
    String name;

    @NotNull
    @NotBlank
    @JsonProperty("description")
    String description;

    @NotNull
    @Min(0)
    @JsonProperty("price")
    Float price;

    @NotNull
    @JsonProperty("active")
    Boolean active;
}
