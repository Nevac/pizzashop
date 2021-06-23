package ch.cagatay.pizzashop.service;

import ch.cagatay.pizzashop.dto.PizzaDto;
import ch.cagatay.pizzashop.exception.ResourceNotFoundException;
import ch.cagatay.pizzashop.model.Pizza;
import ch.cagatay.pizzashop.repository.PizzaRepository;
import ch.cagatay.pizzashop.util.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PizzaService {

    private final String resourceName = "Pizza";
    private final ModelMapper mapper;
    private final PizzaRepository pizzaRepository;

    @Autowired
    public PizzaService(ModelMapper mapper,
                        PizzaRepository pizzaRepository) {
        this.mapper = mapper;
        this.pizzaRepository = pizzaRepository;
    }

    public List<PizzaDto> getPizzas(Boolean showInactive) {
        List<PizzaDto> pizzaDtos = new ArrayList<>();
        if(showInactive) pizzaRepository.findAll().forEach(p -> pizzaDtos.add(mapper.pizzaToPizzaDto(p)));
        else pizzaRepository.findByActive().forEach(p -> pizzaDtos.add(mapper.pizzaToPizzaDto(p)));

        return pizzaDtos;
    }

    public PizzaDto createPizza(PizzaDto pizzaDto) {
        Pizza pizza = mapper.pizzaDtoToPizza(pizzaDto);
        pizza = pizzaRepository.save(pizza);
        return mapper.pizzaToPizzaDto(pizza);
    }

    public PizzaDto getPizza(Long id) throws ResourceNotFoundException {
        Pizza pizza = pizzaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(resourceName));
        return mapper.pizzaToPizzaDto(pizza);
    }

    public void updatePizza(Long id, PizzaDto pizzaDto) throws ResourceNotFoundException {
        Pizza pizza = pizzaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(resourceName));

        pizza.setName(pizzaDto.getName());
        pizza.setDescription(pizzaDto.getDescription());
        pizza.setPrice(pizzaDto.getPrice());
        pizza.setActive(pizzaDto.getActive());

        pizzaRepository.save(pizza);
    }

    public void deletePizza(Long id) throws ResourceNotFoundException {
        Pizza pizza = pizzaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(resourceName));
        pizza.setActive(false);
        pizzaRepository.save(pizza);
    }

    public List<Pizza> findAllById(List<Long> ids) throws ResourceNotFoundException {
        List<Pizza> pizzas = pizzaRepository.findAllById(ids);
        if(ids.size() != pizzas.size()) throw new ResourceNotFoundException(resourceName);
        return pizzas;
    }
}
