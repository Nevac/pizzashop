package ch.cagatay.pizzashop.service;

import ch.cagatay.pizzashop.dto.OrderDtoGet;
import ch.cagatay.pizzashop.dto.OrderDtoPost;
import ch.cagatay.pizzashop.exception.ResourceNotFoundException;
import ch.cagatay.pizzashop.model.Order;
import ch.cagatay.pizzashop.model.Pizza;
import ch.cagatay.pizzashop.repository.OrderRepository;
import ch.cagatay.pizzashop.util.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

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

    public List<OrderDtoGet> getOrders() {
        return orderRepository.findAll().stream().map(mapper::orderToOrderDtoGet)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public OrderDtoGet createOrder(OrderDtoPost orderDtoPost) throws ResourceNotFoundException {
        List<Pizza> pizzas = pizzaService.findAllById(orderDtoPost.getPizzaIds());

        Order order = new Order(
                orderDtoPost.getAddress(),
                orderDtoPost.getPhone(),
                orderDtoPost.getStatus(),
                pizzas
        );

        order = orderRepository.save(order);
        return mapper.orderToOrderDtoGet(order);
    }

    public OrderDtoGet getOrder(Long id) throws ResourceNotFoundException {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(resourceName));
        return mapper.orderToOrderDtoGet(order);
    }

    public void updateOrder(Long id, OrderDtoPost orderDtoPost) throws ResourceNotFoundException {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(resourceName));
        List<Pizza> pizzas = pizzaService.findAllById(orderDtoPost.getPizzaIds());

        order.setAddress(orderDtoPost.getAddress());
        order.setPhone(orderDtoPost.getPhone());
        order.setStatus(orderDtoPost.getStatus());
        order.setPizzas(pizzas);

        orderRepository.save(order);
    }

    public void deleteOrder(Long id) throws ResourceNotFoundException {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(resourceName));
        orderRepository.delete(order);
    }


}
