package ch.cagatay.pizzashop.controller;

import ch.cagatay.pizzashop.dto.OrderDtoOut;
import ch.cagatay.pizzashop.dto.OrderDtoIn;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    @GetMapping
    ResponseEntity<List<OrderDtoOut>> getOrders() {
        throw new UnsupportedOperationException();
    }

    @PostMapping
    ResponseEntity<OrderDtoOut> createOrder(@RequestBody OrderDtoIn orderDtoIn, @PathVariable Long id) {
        throw new UnsupportedOperationException();
    }

    @GetMapping("/{id}")
    ResponseEntity<OrderDtoOut> getOrder(@PathVariable Long id) {
        throw new UnsupportedOperationException();
    }

    @PutMapping("/{id}")
    ResponseEntity<String> updateOrder(@RequestBody OrderDtoIn orderDtoIn, @PathVariable Long id) {
        throw new UnsupportedOperationException();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        throw new UnsupportedOperationException();
    }
}
