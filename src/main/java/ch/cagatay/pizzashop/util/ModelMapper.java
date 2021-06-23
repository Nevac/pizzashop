package ch.cagatay.pizzashop.util;

import ch.cagatay.pizzashop.dto.OrderDtoGet;
import ch.cagatay.pizzashop.dto.OrderDtoPost;
import ch.cagatay.pizzashop.dto.PizzaDto;
import ch.cagatay.pizzashop.model.Order;
import ch.cagatay.pizzashop.model.Pizza;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
@Component
public interface ModelMapper {

    PizzaDto pizzaToPizzaDto(Pizza pizza);
    Pizza pizzaDtoToPizza(PizzaDto pizzy);

    OrderDtoGet orderToOrderDtoGet(Order order);
    Order orderDtoPostToOrder(OrderDtoPost orderDtoPost);
}
