package ch.cagatay.pizzashop;

import ch.cagatay.pizzashop.dto.OrderDtoIn;
import ch.cagatay.pizzashop.dto.OrderDtoOut;
import ch.cagatay.pizzashop.dto.PizzaDto;
import ch.cagatay.pizzashop.model.Order;
import ch.cagatay.pizzashop.model.Pizza;
import ch.cagatay.pizzashop.util.ModelMapper;
import org.mockito.Mockito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class TestUtil {

    /**
     * Sets up a mocked repository by preparing the findAll() and the findById methods.
     * @param repository the mock repository which needs to be set up
     * @param entities the entities which should be contained within the repository
     * @param <T> The type contained within the repository
     */
    @SafeVarargs
    public static <T>void doMockRepoSetup(JpaRepository<T, Long> repository, T... entities){
        Mockito.when(repository.findAll()).thenReturn(Arrays.asList(entities));
        //The id's start with 1 and are then gradually counted upwards in the order that they were passed via the parameters.
        long count = 1;
        for(T entity : entities){
            Mockito.when(repository.findById(count++)).thenReturn(Optional.of(entity));
            Mockito.when(repository.save(entity)).thenReturn(entity);
        }
    }

    /**
     * Generates a spy object which spies on the given pizza and mocks the getID method.
     * This is necessary, since certain checks perform id comparisons and the id cannot be set from outside.
     * @param id the id the spy should return upon calling getId()
     * @param pizza the pizza which should be mocked
     * @return A spy pizza object which returns the given id upon calling getID()
     */
    public static Pizza generateSpyPizza(long id, Pizza pizza){
        Pizza p = Mockito.spy(pizza);
        Mockito.when(p.getId()).thenReturn(id);
        return p;
    }

    /**
     * Generates a spy on PizzaDto object with data from a Pizza object
     * @param pizza the Pizza which should be mocked
     * @return A spy PizzaDto object
     */
    public static PizzaDto generateMockedPizzaDto(Pizza pizza){
        PizzaDto pizzaDto = new PizzaDto();
        pizzaDto.setId(pizza.getId());
        pizzaDto.setName(pizza.getName());
        pizzaDto.setDescription(pizza.getDescription());
        pizzaDto.setPrice(pizza.getPrice());
        pizzaDto.setActive(pizza.isActive());
        return Mockito.spy(pizzaDto);
    }

    /**
     * Generates a spy object which spies on the given order and mocks the getID method.
     * This is necessary, since certain checks perform id comparisons and the id cannot be set from outside.
     * @param id the id the spy should return upon calling getId()
     * @param order the order which should be mocked
     * @return A spy order object which returns the given id upon calling getID()
     */
    public static Order generateSpyOrder(long id, Order order){
        Order o = Mockito.spy(order);
        Mockito.when(o.getId()).thenReturn(id);
        return o;
    }

    /**
     * Generates a spy on OrderDtoIn object with data from an Order object
     * @param order the order which should be mocked
     * @return A spy OderDtoIn object
     */
    public static OrderDtoIn generateMockedOrderDtoIn(Order order){
        OrderDtoIn orderDto = new OrderDtoIn();
        orderDto.setId(order.getId());
        orderDto.setAddress(order.getAddress());
        orderDto.setPhone(order.getPhone());
        orderDto.setStatus(order.getStatus());
        orderDto.setPizzaIds(order.getPizzas().stream().map(Pizza::getId).collect(Collectors.toList()));
        return Mockito.spy(orderDto);
    }

    /**
     * Generates a spy on OrderDtoOut object with data from an Order object
     * @param order the order which should be mocked
     * @return A spy OderDtoOut object
     */
    public static OrderDtoOut generateMockedOrderDtoOut(Order order){
        OrderDtoOut orderDto = new OrderDtoOut();
        orderDto.setId(order.getId());
        orderDto.setAddress(order.getAddress());
        orderDto.setPhone(order.getPhone());
        orderDto.setStatus(order.getStatus());
        orderDto.setPizzas(order.getPizzas().stream()
                .map(TestUtil::generateMockedPizzaDto).collect(Collectors.toList()));
        return Mockito.spy(orderDto);
    }
}