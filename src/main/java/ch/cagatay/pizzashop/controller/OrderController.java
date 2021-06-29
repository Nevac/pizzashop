package ch.cagatay.pizzashop.controller;

import ch.cagatay.pizzashop.dto.OrderDtoOut;
import ch.cagatay.pizzashop.dto.OrderDtoIn;
import ch.cagatay.pizzashop.dto.PizzaDto;
import ch.cagatay.pizzashop.exception.ResourceNotFoundException;
import ch.cagatay.pizzashop.model.Order;
import ch.cagatay.pizzashop.model.Pizza;
import ch.cagatay.pizzashop.service.OrderService;
import ch.cagatay.pizzashop.service.PizzaService;
import ch.cagatay.pizzashop.service.PizzaShopService;
import ch.cagatay.pizzashop.specifications.SpecificationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    private final String resourceName = "Order";
    private final PizzaShopService<Order, OrderDtoIn, OrderDtoOut> orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    ResponseEntity<List<OrderDtoOut>> getOrder(@RequestParam(required = false) String search) {
        Specification<Order> spec = SpecificationBuilder.buildSpecificationFromString(search);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.getAll(spec));
    }

    @PostMapping
    ResponseEntity<OrderDtoOut> createOrder(@RequestBody OrderDtoIn newOrderDto, @PathVariable Long id)
            throws ResourceNotFoundException {
        OrderDtoOut orderDtoOut = orderService.create(newOrderDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderDtoOut);
    }

    @GetMapping("/{id}")
    @ResponseBody
    OrderDtoOut getOrder(@PathVariable Long id) throws ResourceNotFoundException {
        return orderService.get(id);
    }

    @PutMapping("/{id}")
    ResponseEntity<String> updateOrder(@RequestBody OrderDtoIn updatedOrderDto, @PathVariable Long id)
            throws ResourceNotFoundException {
        orderService.update(id, updatedOrderDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(String.format("Updated %s", resourceName));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteOrder(@PathVariable Long id) throws ResourceNotFoundException {
        orderService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(String.format("Deleted %s", resourceName));
    }
}
