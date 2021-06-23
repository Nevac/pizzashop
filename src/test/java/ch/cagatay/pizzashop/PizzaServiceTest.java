package ch.cagatay.pizzashop;

import ch.cagatay.pizzashop.dto.PizzaDto;
import ch.cagatay.pizzashop.exception.ResourceNotFoundException;
import ch.cagatay.pizzashop.model.Pizza;
import ch.cagatay.pizzashop.repository.PizzaRepository;
import ch.cagatay.pizzashop.service.PizzaService;
import ch.cagatay.pizzashop.util.ModelMapper;
import ch.cagatay.pizzashop.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class PizzaServiceTest {

    @Mock
    PizzaRepository pizzaRepository;
    @Mock
    ModelMapper modelMapper;
    @InjectMocks
    PizzaService pizzaService;

    private Pizza pizza1;
    private Pizza pizza2;
    private Pizza pizza3;
    private Pizza pizza4;
    private Pizza pizza5;

    private PizzaDto pizza1Dto;
    private PizzaDto pizza2Dto;
    private PizzaDto pizza3Dto;
    private PizzaDto pizza4Dto;
    private PizzaDto pizza5Dto;

    private List<Long> pizzaIdList;
    private List<Long> pizzaIdListWithNonExistent;

    @Before
    public void setUp() {

        //Generate Pizza Spies
        pizza1 = generateSpyPizza(1L,
                new Pizza("Margharitta", "Cheese and Tomato", 12.0f, true));
        pizza1Dto = generateMockedPizzaDto(pizza1);

        pizza2 = generateSpyPizza(2L,
                new Pizza("Proscuitto", "Pork Salami", 15.0f, true));
        pizza2Dto = generateMockedPizzaDto(pizza2);

        pizza3 = generateSpyPizza(3L,
                new Pizza("Funghi", "Mushrooms and Cheese", 15.0f, true));
        pizza3Dto = generateMockedPizzaDto(pizza3);

        pizza4 = generateSpyPizza(4L,
                new Pizza("Hawaii", "Pineapples do not belong onto pizzas", 15.0f, false));
        pizza4Dto = generateMockedPizzaDto(pizza4);

        //Pizza5 is not persisted in the pizzaRepository mock
        pizza5 = generateSpyPizza(5L,
                new Pizza("Imagination", "Non existent Pizza", 15.0f, false));
        pizza5Dto = generateMockedPizzaDto(pizza5);

        //Mock common Repository methods
        TestUtil.doMockRepoSetup(pizzaRepository, pizza1, pizza2, pizza3, pizza4);

        //Mock methods unique to this repository
        Mockito.when(pizzaRepository.findByActive()).thenReturn(Arrays.asList(pizza1, pizza2, pizza3));

        pizzaIdList = Arrays.asList(pizza1.getId(), pizza2.getId(), pizza3.getId());
        Mockito.when(pizzaRepository.findAllById(pizzaIdList))
                .thenReturn(Arrays.asList(pizza1, pizza2, pizza3));
        pizzaIdListWithNonExistent = Arrays.asList(pizza1.getId(), pizza2.getId(), pizza5.getId());
        Mockito.when(pizzaRepository.findAllById(pizzaIdListWithNonExistent))
                .thenReturn(Arrays.asList(pizza1, pizza2));

        //Mock mapper methods
        generateMockedMapperMethods(modelMapper);
    }

    /**
     * Generates a spy object which spies on the given pizza and mocks the getID method.
     * This is necessary, since certain checks perform id comparisons and the id cannot be set from outside.
     * @param id the id the spy should return upon calling getId()
     * @param pizza the pizza which should be mocked
     * @return A spy pizza object which returns the given id upon calling getID()
     */
    private Pizza generateSpyPizza(long id, Pizza pizza){
        Pizza p = Mockito.spy(pizza);
        Mockito.when(p.getId()).thenReturn(id);
        return p;
    }

    /**
     * Generates a spy object which spies on the given pizzaDto
     * @param pizza the pizza which should be mocked
     * @return A spy pizzaDto object
     */
    private PizzaDto generateMockedPizzaDto(Pizza pizza){
        PizzaDto pizzaDto = new PizzaDto();
        pizzaDto.setId(pizza.getId());
        pizzaDto.setName(pizza.getName());
        pizzaDto.setDescription(pizza.getDescription());
        pizzaDto.setPrice(pizza.getPrice());
        pizzaDto.setActive(pizza.isActive());
        return Mockito.spy(pizzaDto);
    }

    /**
     * Generates mapper mock methods
     * @param modelMapper the modelMapper which should be mocked
     */
    private void generateMockedMapperMethods(ModelMapper modelMapper){
        Mockito.when(modelMapper.pizzaToPizzaDto(pizza1)).thenReturn(pizza1Dto);
        Mockito.when(modelMapper.pizzaToPizzaDto(pizza2)).thenReturn(pizza2Dto);
        Mockito.when(modelMapper.pizzaToPizzaDto(pizza3)).thenReturn(pizza3Dto);
        Mockito.when(modelMapper.pizzaToPizzaDto(pizza4)).thenReturn(pizza4Dto);
    }

    @Test
    public void testGetAllPizzas() {
        List<PizzaDto> pizzaDtos = pizzaService.getPizzas(true);
        assertEquals(4, pizzaDtos.size());
    }

    @Test
    public void testGetAllPizzasOnlyActive() {
        List<PizzaDto> pizzaDtos = pizzaService.getPizzas(false);
        assertEquals(3, pizzaDtos.size());
    }

    @Test
    public void testGetPizza() {
        try {
            PizzaDto pizzaDto = pizzaService.getPizza(pizza1Dto.getId());
            assertEquals(pizza1.getId(), pizzaDto.getId());
        } catch (ResourceNotFoundException e) {
            fail();
        }
    }

    @Test
    public void testGetNonExistentPizza() {
        assertThrows(ResourceNotFoundException.class, () -> {
            pizzaService.getPizza(pizza5Dto.getId());
        });
    }

    @Test
    public void testUpdatePizza() {
        try {
            PizzaDto newPizzaDto = new PizzaDto();
            newPizzaDto.setName("Padrone");
            newPizzaDto.setDescription("Beef and Oil");
            newPizzaDto.setPrice(18.5f);
            newPizzaDto.setActive(true);

            pizzaService.updatePizza(pizza1.getId(), newPizzaDto);

            assertEquals(newPizzaDto.getName(), pizza1.getName());
            assertEquals(newPizzaDto.getDescription(), pizza1.getDescription());
            assertEquals(newPizzaDto.getPrice(), pizza1.getPrice(), 0.001);
            assertEquals(newPizzaDto.isActive(), pizza1.isActive());
        } catch (ResourceNotFoundException e) {
            fail();
        }
    }

    @Test
    public void testUpdateNonExistentPizza() {
        assertThrows(ResourceNotFoundException.class, () -> {
            PizzaDto newPizzaDto = new PizzaDto();
            newPizzaDto.setName("Padrone");
            newPizzaDto.setDescription("Beef and Oil");
            newPizzaDto.setPrice(18.5f);
            newPizzaDto.setActive(true);

            pizzaService.updatePizza(pizza5.getId(), newPizzaDto);
        });
    }

    @Test
    public void deleteActivePizza() {
        assertTrue(pizza1.isActive());
        try {
            pizzaService.deletePizza(pizza1.getId());
            assertFalse(pizza1.isActive());
        } catch (ResourceNotFoundException e) {
            fail();
        }
    }

    @Test
    public void testDeleteInactivePizza() {
        assertFalse(pizza4.isActive());
        try {
            pizzaService.deletePizza(pizza4.getId());
            assertFalse(pizza4.isActive());
        } catch (ResourceNotFoundException e) {
            fail();
        }
    }

    @Test
    public void testDeleteNonExistingPizzaPizza() {
        assertFalse(pizza5.isActive());
        assertThrows(ResourceNotFoundException.class, () -> {
            pizzaService.deletePizza(pizza5Dto.getId());
        });
    }

    @Test
    public void testFindAllByIds() {
        try {
            List<Pizza> pizzas = pizzaService.findAllById(pizzaIdList);
            assertEquals(pizzaIdList.size(), pizzas.size());
        } catch (ResourceNotFoundException e) {
            fail();
        }
    }

    @Test
    public void testFindAllByIdsWithNonExistingIds() {
        assertThrows(ResourceNotFoundException.class, () -> pizzaService.findAllById(pizzaIdListWithNonExistent));
    }
}
