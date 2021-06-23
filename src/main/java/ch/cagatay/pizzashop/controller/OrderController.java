package ch.cagatay.pizzashop.controller;

import ch.cagatay.pizzashop.dto.OrderDtoGet;
import ch.cagatay.pizzashop.dto.OrderDtoPost;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    @GetMapping
    ResponseEntity<List<OrderDtoGet>> getOrders() {
        throw new UnsupportedOperationException();
    }

    @PostMapping
    ResponseEntity<OrderDtoGet> createOrder(@RequestBody OrderDtoPost orderDtoPost, @PathVariable Long id) {
        throw new UnsupportedOperationException();
    }

    @GetMapping("/{id}")
    ResponseEntity<OrderDtoGet> getOrder(@PathVariable Long id) {
        throw new UnsupportedOperationException();
    }

    @PutMapping("/{id}")
    ResponseEntity<String> updateOrder(@RequestBody OrderDtoPost orderDtoPost, @PathVariable Long id) {
        throw new UnsupportedOperationException();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        throw new UnsupportedOperationException();
    }
}
