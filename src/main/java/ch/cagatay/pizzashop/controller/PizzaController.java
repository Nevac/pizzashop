package ch.cagatay.pizzashop.controller;

import ch.cagatay.pizzashop.dto.PizzaDto;
import ch.cagatay.pizzashop.model.Pizza;
import ch.cagatay.pizzashop.service.PizzaService;
import ch.cagatay.pizzashop.specifications.SpecificationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/pizza")
public class PizzaController {

    private final PizzaService pizzaService;

    @Autowired
    public PizzaController(PizzaService pizzaService) {
        this.pizzaService = pizzaService;
    }

    @GetMapping
    ResponseEntity<List<PizzaDto>> getPizzas(@RequestParam(required = false) String search) {
        Specification<Pizza> spec = SpecificationBuilder.buildSpecificationFromString(search);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pizzaService.getAll(spec));
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
