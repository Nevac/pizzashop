package ch.cagatay.pizzashop.service;

import ch.cagatay.pizzashop.dto.OrderDtoOut;
import ch.cagatay.pizzashop.dto.OrderDtoIn;
import ch.cagatay.pizzashop.exception.ResourceNotFoundException;
import ch.cagatay.pizzashop.model.Order;
import ch.cagatay.pizzashop.model.Pizza;
import ch.cagatay.pizzashop.repository.OrderRepository;
import ch.cagatay.pizzashop.util.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService implements PizzaShopService<Order, OrderDtoIn, OrderDtoOut> {

    private final String resourceName = "Order";
    private final ModelMapper mapper;
    private final OrderRepository orderRepository;
    private final PizzaService pizzaService;

    @Autowired
    public OrderService(ModelMapper mapper,
                        OrderRepository orderRepository,
                        PizzaService pizzaService) {
        this.mapper = mapper;
        this.orderRepository = orderRepository;
        this.pizzaService = pizzaService;
    }

    @Override
    public List<OrderDtoOut> getAll(Specification<Order> spec) {
        if(spec != null) {
            try {
                return orderRepository.findAll(spec).stream().map(mapper::orderToOrderDtoOut)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                return new ArrayList<>();
            }
        }
        return orderRepository.findAll().stream().map(mapper::orderToOrderDtoOut).collect(Collectors.toList());
    }

    @Override
    public OrderDtoOut create(OrderDtoIn orderDtoIn) throws ResourceNotFoundException {
        List<Pizza> pizzas = pizzaService.getAllByIds(orderDtoIn.getPizzaIds());

        Order order = new Order(
                orderDtoIn.getAddress(),
                orderDtoIn.getPhone(),
                orderDtoIn.getStatus(),
                pizzas
        );

        order = orderRepository.save(order);
        return mapper.orderToOrderDtoOut(order);
    }

    @Override
    public OrderDtoOut get(Long id) throws ResourceNotFoundException {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(resourceName));
        return mapper.orderToOrderDtoOut(order);
    }

    @Override
    public void update(Long id, OrderDtoIn orderDtoIn) throws ResourceNotFoundException {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(resourceName));
        List<Pizza> pizzas = pizzaService.getAllByIds(orderDtoIn.getPizzaIds());

        order.setAddress(orderDtoIn.getAddress());
        order.setPhone(orderDtoIn.getPhone());
        order.setStatus(orderDtoIn.getStatus());
        order.setPizzas(pizzas);

        orderRepository.save(order);
    }

    @Override
    public void delete(Long id) throws ResourceNotFoundException {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(resourceName));
        orderRepository.delete(order);
    }

    @Override
    public List<Order> getAllByIds(List<Long> ids) throws ResourceNotFoundException {
        List<Order> orders = orderRepository.findAllById(ids);
        if(ids.size() != orders.size()) throw new ResourceNotFoundException(resourceName);
        return orders;
    }
}
