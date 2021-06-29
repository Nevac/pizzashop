package ch.cagatay.pizzashop;

import ch.cagatay.pizzashop.dto.PizzaDto;
import ch.cagatay.pizzashop.exception.ResourceNotFoundException;
import ch.cagatay.pizzashop.model.Pizza;
import ch.cagatay.pizzashop.repository.PizzaRepository;
import ch.cagatay.pizzashop.service.PizzaService;
import ch.cagatay.pizzashop.specifications.GeneralSpecification;
import ch.cagatay.pizzashop.specifications.SearchCriteria;
import ch.cagatay.pizzashop.specifications.SearchOperation;
import ch.cagatay.pizzashop.util.ModelMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.jpa.domain.Specification;
import org.hamcrest.MatcherAssert;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.hasItem;

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

    @Before
    public void setUp() {

        //Generate Pizza Spies
        pizza1 = TestUtil.generateSpyPizza(1L,
                new Pizza("Margharita", "Cheese and Tomato", 12.0f, true));
        pizza1Dto = TestUtil.generateMockedPizzaDto(pizza1);

        pizza2 = TestUtil.generateSpyPizza(2L,
                new Pizza("Proscuitto", "Pork Salami", 15.0f, true));
        pizza2Dto = TestUtil.generateMockedPizzaDto(pizza2);

        pizza3 = TestUtil.generateSpyPizza(3L,
                new Pizza("Funghi", "Mushrooms and Cheese", 15.0f, true));
        pizza3Dto = TestUtil.generateMockedPizzaDto(pizza3);

        pizza4 = TestUtil.generateSpyPizza(4L,
                new Pizza("Hawaii", "Pineapples do not belong onto pizzas", 15.0f, false));
        pizza4Dto = TestUtil.generateMockedPizzaDto(pizza4);

        //Pizza5 is not persisted in the pizzaRepository mock
        pizza5 = TestUtil.generateSpyPizza(5L,
                new Pizza("Imagination", "Non existent Pizza", 15.0f, false));
        pizza5Dto = TestUtil.generateMockedPizzaDto(pizza5);

        //Mock common Repository methods
        TestUtil.doMockRepoSetup(pizzaRepository, pizza1, pizza2, pizza3, pizza4);

        //Mock mapper methods
        generateMockedMapperMethods(modelMapper);
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
    public void ReturnAllPizzas() {
        List<PizzaDto> pizzaDtos = pizzaService.getAll(null);
        assertEquals(4, pizzaDtos.size());
        MatcherAssert.assertThat(pizzaDtos, hasItem(pizza1Dto));
        MatcherAssert.assertThat(pizzaDtos, hasItem(pizza2Dto));
        MatcherAssert.assertThat(pizzaDtos, hasItem(pizza3Dto));
        MatcherAssert.assertThat(pizzaDtos, hasItem(pizza4Dto));
    }

    @Test
    public void ReturnAllPizzasOnlyThatMatchValidQuery() {
        Specification<Pizza> spec =
                new GeneralSpecification<>(new SearchCriteria("active", SearchOperation.EQUALITY, "true"));
        Mockito.when(pizzaRepository.findAll(spec)).thenReturn(Arrays.asList(pizza1, pizza2, pizza3));
        List<PizzaDto> pizzaDtos = pizzaService.getAll(spec);
        assertEquals(3, pizzaDtos.size());
        MatcherAssert.assertThat(pizzaDtos, hasItem(pizza1Dto));
        MatcherAssert.assertThat(pizzaDtos, hasItem(pizza2Dto));
        MatcherAssert.assertThat(pizzaDtos, hasItem(pizza3Dto));
    }

    @Test
    public void ReturnEmptyListIfQueryIsFaulty() {
        Specification<Pizza> specSearchCriteriaHasNull =
                new GeneralSpecification<>(new SearchCriteria(null, null, null));
        Specification<Pizza> specSearchCriteriaHasNonExistentField =
                new GeneralSpecification<>(new SearchCriteria("nonExistent", SearchOperation.EQUALITY, "true"));
        Mockito.when(pizzaRepository.findAll(specSearchCriteriaHasNull))
                .thenThrow(NullPointerException.class);
        Mockito.when(pizzaRepository.findAll(specSearchCriteriaHasNonExistentField))
                .thenThrow(InvalidDataAccessApiUsageException.class);

        assertEquals(pizzaService.getAll(specSearchCriteriaHasNull).size(), 0);
        assertEquals(pizzaService.getAll(specSearchCriteriaHasNonExistentField).size(), 0);

    }

    @Test
    public void GetPizza() {
        try {
            PizzaDto pizzaDto = pizzaService.get(pizza1Dto.getId());
            assertEquals(pizza1.getId(), pizzaDto.getId());
        } catch (ResourceNotFoundException e) {
            fail();
        }
    }

    @Test
    public void ThrowResourceNotFoundExceptionIfGetPizzaIdDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> {
            pizzaService.get(pizza5Dto.getId());
        });
    }

    @Test
    public void UpdatePizza() {
        try {
            PizzaDto newPizzaDto = new PizzaDto();
            newPizzaDto.setName("Padrone");
            newPizzaDto.setDescription("Beef and Oil");
            newPizzaDto.setPrice(18.5f);
            newPizzaDto.setActive(true);

            pizzaService.update(pizza1.getId(), newPizzaDto);

            assertEquals(newPizzaDto.getName(), pizza1.getName());
            assertEquals(newPizzaDto.getDescription(), pizza1.getDescription());
            assertEquals(newPizzaDto.getPrice(), pizza1.getPrice(), 0.001);
            assertEquals(newPizzaDto.getActive(), pizza1.getActive());
        } catch (ResourceNotFoundException e) {
            fail();
        }
    }

    @Test
    public void ThrowResourceNotFoundExceptionIfUpdatePizzaIdDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> {
            PizzaDto newPizzaDto = new PizzaDto();
            newPizzaDto.setName("Padrone");
            newPizzaDto.setDescription("Beef and Oil");
            newPizzaDto.setPrice(18.5f);
            newPizzaDto.setActive(true);

            pizzaService.update(pizza5.getId(), newPizzaDto);
        });
    }

    @Test
    public void SetPizzaInactiveIfDeletedWithActiveTrue() {
        assertTrue(pizza1.getActive());
        try {
            pizzaService.delete(pizza1.getId());
            assertFalse(pizza1.getActive());
        } catch (ResourceNotFoundException e) {
            fail();
        }
    }

    @Test
    public void KeepPizzaInactiveIfDeletedWithActiveFalse() {
        assertFalse(pizza4.getActive());
        try {
            pizzaService.delete(pizza4.getId());
            assertFalse(pizza4.getActive());
        } catch (ResourceNotFoundException e) {
            fail();
        }
    }

    @Test
    public void ThrowResourceNotFoundExceptionIfDeletePizzaIdDoesNotExist() {
        assertFalse(pizza5.getActive());
        assertThrows(ResourceNotFoundException.class, () -> {
            pizzaService.delete(pizza5Dto.getId());
        });
    }

    @Test
    public void ReturnPizzaListIfAllIdsArePresent() {
        List<Long> pizzaIdList = Arrays.asList(pizza1.getId(), pizza2.getId(), pizza3.getId());
        Mockito.when(pizzaRepository.findAllById(pizzaIdList))
                .thenReturn(Arrays.asList(pizza1, pizza2, pizza3));
        try {
            List<Pizza> pizzas = pizzaService.getAllByIds(pizzaIdList);
            MatcherAssert.assertThat(pizzas, hasItem(pizza1));
            MatcherAssert.assertThat(pizzas, hasItem(pizza2));
            MatcherAssert.assertThat(pizzas, hasItem(pizza3));
        } catch (ResourceNotFoundException e) {
            fail();
        }
    }

    @Test
    public void ThrowResourceNotFoundExceptionIfAtLeastOnePizzaIdInListDoesNotExist() {
        List<Long> pizzaIdListWithNonExistent = Arrays.asList(pizza1.getId(), pizza2.getId(), pizza5.getId());
        Mockito.when(pizzaRepository.findAllById(pizzaIdListWithNonExistent))
                .thenReturn(Arrays.asList(pizza1, pizza2));
        assertThrows(ResourceNotFoundException.class, () -> pizzaService.getAllByIds(pizzaIdListWithNonExistent));
    }
}
