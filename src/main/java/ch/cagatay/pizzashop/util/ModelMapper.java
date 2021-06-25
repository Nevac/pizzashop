package ch.cagatay.pizzashop.util;

import ch.cagatay.pizzashop.dto.OrderDtoOut;
import ch.cagatay.pizzashop.dto.OrderDtoIn;
import ch.cagatay.pizzashop.dto.PizzaDto;
import ch.cagatay.pizzashop.model.Order;
import ch.cagatay.pizzashop.model.Pizza;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
@Component("mapper")
public interface ModelMapper {

    PizzaDto pizzaToPizzaDto(Pizza pizza);
    Pizza pizzaDtoToPizza(PizzaDto pizza);

    OrderDtoOut orderToOrderDtoOut(Order order);
    Order orderDtoOutToOrder(OrderDtoIn orderDtoIn);
}
