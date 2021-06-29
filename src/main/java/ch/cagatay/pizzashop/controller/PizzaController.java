package ch.cagatay.pizzashop.controller;

import ch.cagatay.pizzashop.dto.PizzaDto;
import ch.cagatay.pizzashop.exception.ResourceNotFoundException;
import ch.cagatay.pizzashop.model.Pizza;
import ch.cagatay.pizzashop.service.PizzaService;
import ch.cagatay.pizzashop.service.PizzaShopService;
import ch.cagatay.pizzashop.specifications.SpecificationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/pizza")
public class PizzaController {

    private final String resourceName = "Pizza";
    private final PizzaShopService<Pizza, PizzaDto, PizzaDto> pizzaService;

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
    ResponseEntity<PizzaDto> createPizza(@RequestBody @Valid PizzaDto newPizzaDto)
            throws ResourceNotFoundException {
        PizzaDto pizzaDto = pizzaService.create(newPizzaDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(pizzaDto);
    }

    @GetMapping("/{id}")
    @ResponseBody
    PizzaDto getPizza(@PathVariable Long id) throws ResourceNotFoundException {
        return pizzaService.get(id);
    }

    @PutMapping("/{id}")
    ResponseEntity<String> updatePizza(@RequestBody @Valid PizzaDto updatedPizzaDto, @PathVariable Long id)
            throws ResourceNotFoundException {
        pizzaService.update(id, updatedPizzaDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<String> deletePizza(@PathVariable Long id) throws ResourceNotFoundException {
        pizzaService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
