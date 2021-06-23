package ch.cagatay.pizzashop.controller;

import ch.cagatay.pizzashop.dto.PizzaDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/pizza")
public class PizzaController {

    @GetMapping
    ResponseEntity<List<PizzaDto>> getPizzas(@RequestParam(required = false) boolean showInactive) {
        throw new UnsupportedOperationException();
    }

    @PostMapping
    ResponseEntity<PizzaDto> createPizza(@RequestBody PizzaDto pizzaDto, @PathVariable Long id) {
        throw new UnsupportedOperationException();
    }

    @GetMapping("/{id}")
    ResponseEntity<PizzaDto> getPizza(@PathVariable Long id) {
        throw new UnsupportedOperationException();
    }

    @PutMapping("/{id}")
    ResponseEntity<String> updatePizza(@RequestBody PizzaDto pizzaDto, @PathVariable Long id) {
        throw new UnsupportedOperationException();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<String> deletePizza(@PathVariable Long id) {
        throw new UnsupportedOperationException();
    }
}
