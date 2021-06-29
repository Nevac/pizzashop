package ch.cagatay.pizzashop.service;

import ch.cagatay.pizzashop.dto.PizzaDto;
import ch.cagatay.pizzashop.exception.ResourceNotFoundException;
import ch.cagatay.pizzashop.model.Pizza;
import ch.cagatay.pizzashop.repository.PizzaRepository;
import ch.cagatay.pizzashop.util.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PizzaService implements PizzaShopService<Pizza, PizzaDto, PizzaDto> {

    private final String resourceName = "Pizza";
    private final ModelMapper mapper;
    private final PizzaRepository pizzaRepository;

    @Autowired
    public PizzaService(PizzaRepository pizzaRepository,
                        ModelMapper mapper) {
        this.mapper = mapper;
        this.pizzaRepository = pizzaRepository;
    }

    @Override
    public List<PizzaDto> getAll(Specification<Pizza> spec) {
        if(spec != null) {
            try {
                return pizzaRepository.findAll(spec).stream().map(mapper::pizzaToPizzaDto).collect(Collectors.toList());
            } catch (Exception e) {
                return new ArrayList<>();
            }
        }
        return pizzaRepository.findAll().stream().map(mapper::pizzaToPizzaDto).collect(Collectors.toList());
    }

    @Override
    public PizzaDto create(PizzaDto pizzaDto) {
        Pizza pizza = mapper.pizzaDtoToPizza(pizzaDto);
        pizza = pizzaRepository.save(pizza);
        return mapper.pizzaToPizzaDto(pizza);
    }

    @Override
    public PizzaDto get(Long id) throws ResourceNotFoundException {
        Pizza pizza = pizzaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(resourceName));
        return mapper.pizzaToPizzaDto(pizza);
    }

    @Override
    public void update(Long id, PizzaDto pizzaDto) throws ResourceNotFoundException {
        Pizza pizza = pizzaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(resourceName));

        pizza.setName(pizzaDto.getName());
        pizza.setDescription(pizzaDto.getDescription());
        pizza.setPrice(pizzaDto.getPrice());
        pizza.setActive(pizzaDto.getActive());
        pizzaRepository.save(pizza);
    }

    @Override
    public void delete(Long id) throws ResourceNotFoundException {
        Pizza pizza = pizzaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(resourceName));
        pizza.setActive(false);
        pizzaRepository.save(pizza);
    }

    @Override
    public List<Pizza> getAllByIds(List<Long> ids) throws ResourceNotFoundException {
        List<Pizza> pizzas = pizzaRepository.findAllById(ids);
        if(ids.size() != pizzas.size()) throw new ResourceNotFoundException(resourceName);
        return pizzas;
    }
}
