package ch.cagatay.pizzashop;

import ch.cagatay.pizzashop.dto.OrderDtoOut;
import ch.cagatay.pizzashop.dto.OrderDtoIn;
import ch.cagatay.pizzashop.dto.PizzaDto;
import ch.cagatay.pizzashop.exception.ResourceNotFoundException;
import ch.cagatay.pizzashop.model.Order;
import ch.cagatay.pizzashop.model.OrderStatus;
import ch.cagatay.pizzashop.model.Pizza;
import ch.cagatay.pizzashop.repository.OrderRepository;
import ch.cagatay.pizzashop.service.OrderService;
import ch.cagatay.pizzashop.service.PizzaService;
import ch.cagatay.pizzashop.specifications.GeneralSpecification;
import ch.cagatay.pizzashop.specifications.SearchCriteria;
import ch.cagatay.pizzashop.specifications.SearchOperation;
import ch.cagatay.pizzashop.util.ModelMapper;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {

    @Mock
    ModelMapper modelMapper;
    @Mock
    OrderRepository orderRepository;
    @Mock
    PizzaService pizzaService;
    @InjectMocks
    OrderService orderService;

    private Order order1;
    private Order order2;
    private Order order3;
    private Order order4;

    private OrderDtoIn order1DtoIn;
    private OrderDtoIn order2DtoIn;
    private OrderDtoIn order3DtoIn;
    private OrderDtoIn order4DtoIn;

    private OrderDtoOut order1DtoOut;
    private OrderDtoOut order2DtoOut;
    private OrderDtoOut order3DtoOut;
    private OrderDtoOut order4DtoOut;


    private Pizza pizza1;
    private Pizza pizza2;
    private Pizza pizza3;

    private PizzaDto pizza1Dto;
    private PizzaDto pizza2Dto;
    private PizzaDto pizza3Dto;

    @Before
    public void setUp() throws Exception {

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

        //Generate Order Spies
        order1 = TestUtil.generateSpyOrder(1L,
                new Order("Address 1", "0791111111", OrderStatus.PENDING,
                        Arrays.asList(pizza1, pizza2)));
        order1DtoIn = TestUtil.generateMockedOrderDtoIn(order1);
        order1DtoOut = TestUtil.generateMockedOrderDtoOut(order1);

        order2 = TestUtil.generateSpyOrder(2L,
                new Order("Address 2", "0792222222", OrderStatus.IN_PROCESS,
                        Collections.singletonList(pizza2)));
        order2DtoIn = TestUtil.generateMockedOrderDtoIn(order2);
        order2DtoOut = TestUtil.generateMockedOrderDtoOut(order2);

        order3 = TestUtil.generateSpyOrder(3L,
                new Order("Address 3", "0793333333", OrderStatus.IN_DELIVERY,
                        Arrays.asList(pizza2, pizza3)));
        order3DtoIn = TestUtil.generateMockedOrderDtoIn(order3);
        order3DtoOut = TestUtil.generateMockedOrderDtoOut(order3);

        order4 = TestUtil.generateSpyOrder(4L,
                new Order("Address 4", "0794444444", OrderStatus.IN_DELIVERY,
                        Arrays.asList(pizza2, pizza3)));
        order4DtoIn = TestUtil.generateMockedOrderDtoIn(order4);
        order4DtoOut = TestUtil.generateMockedOrderDtoOut(order4);

        //Mock common Repository methods
        TestUtil.doMockRepoSetup(orderRepository, order1, order2, order3);

        //Mock mapper methods
        generateMockedMapperMethods(modelMapper);
    }

    /**
     * Generates mapper mock methods
     * @param modelMapper the modelMapper which should be mocked
     */
    private void generateMockedMapperMethods(ModelMapper modelMapper){
        Mockito.when(modelMapper.orderToOrderDtoOut(order1)).thenReturn(order1DtoOut);
        Mockito.when(modelMapper.orderToOrderDtoOut(order2)).thenReturn(order2DtoOut);
        Mockito.when(modelMapper.orderToOrderDtoOut(order3)).thenReturn(order3DtoOut);
    }

    @Test
    public void ReturnAllOrders() {
        List<OrderDtoOut> orderDtoOuts = orderService.getAll(null);
        assertEquals(3, orderDtoOuts.size());
        MatcherAssert.assertThat(orderDtoOuts, hasItem(order1DtoOut));
        MatcherAssert.assertThat(orderDtoOuts, hasItem(order2DtoOut));
        MatcherAssert.assertThat(orderDtoOuts, hasItem(order3DtoOut));
    }

    @Test
    public void ReturnAllPizzasOnlyThatMatchValidQuery() {
        Specification<Order> spec =
                new GeneralSpecification<>(new SearchCriteria("address", SearchOperation.EQUALITY, "Address 1"));
        Mockito.when(orderRepository.findAll(spec)).thenReturn(Collections.singletonList(order1));
        List<OrderDtoOut> orderDtoOuts = orderService.getAll(spec);
        assertEquals(1, orderDtoOuts.size());
        MatcherAssert.assertThat(orderDtoOuts, hasItem(order1DtoOut));
        MatcherAssert.assertThat(orderDtoOuts, not(hasItem(order2DtoOut)));
        MatcherAssert.assertThat(orderDtoOuts, not(hasItem(order3DtoOut)));
    }

    @Test
    public void ReturnEmptyListIfQueryIsFaulty() {
        Specification<Order> specSearchCriteriaHasNull =
                new GeneralSpecification<>(new SearchCriteria(null, null, null));
        Specification<Order> specSearchCriteriaHasNonExistentField =
                new GeneralSpecification<>(new SearchCriteria("nonExistent", SearchOperation.EQUALITY, "true"));
        Mockito.when(orderRepository.findAll(specSearchCriteriaHasNull))
                .thenThrow(NullPointerException.class);
        Mockito.when(orderRepository.findAll(specSearchCriteriaHasNonExistentField))
                .thenThrow(InvalidDataAccessApiUsageException.class);

        assertEquals(orderService.getAll(specSearchCriteriaHasNull).size(), 0);
        assertEquals(orderService.getAll(specSearchCriteriaHasNonExistentField).size(), 0);

    }

    @Test
    public void GetOrder() {
        try {
            OrderDtoOut orderDtoOut = orderService.get(order1.getId());
            assertEquals(order1.getId(), orderDtoOut.getId());
        } catch (ResourceNotFoundException e) {
            fail();
        }
    }

    @Test
    public void ThrowResourceNotFoundExceptionIfGetOrderIdDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.get(order4.getId());
        });
    }

    @Test
    public void UpdateOrder() {
        try {
            List<Long> ids = Arrays.asList(1L, 3L);
            Mockito.when(pizzaService.getAllByIds(ids)).thenReturn(Arrays.asList(pizza1, pizza3));
            OrderDtoIn newOrderDtoIn = new OrderDtoIn();
            newOrderDtoIn.setAddress("Address new");
            newOrderDtoIn.setPhone("0799999999");
            newOrderDtoIn.setStatus(OrderStatus.COMPLETED);
            newOrderDtoIn.setPizzaIds(ids);

            orderService.update(order1.getId(), newOrderDtoIn);

            assertEquals(newOrderDtoIn.getAddress(), order1.getAddress());
            assertEquals(newOrderDtoIn.getPhone(), order1.getPhone());
            assertEquals(newOrderDtoIn.getStatus(), order1.getStatus());
            assertArrayEquals(newOrderDtoIn.getPizzaIds().toArray(),
                    order1.getPizzas().stream().map(Pizza::getId).toArray());
        } catch (ResourceNotFoundException e) {
            fail();
        }
    }

    @Test
    public void ThrowResourceNotFoundExceptionIfUpdateOrderIdDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> {
            OrderDtoIn newOrderDtoIn = new OrderDtoIn();
            newOrderDtoIn.setAddress("Address new");
            newOrderDtoIn.setPhone("0799999999");
            newOrderDtoIn.setStatus(OrderStatus.COMPLETED);
            newOrderDtoIn.setPizzaIds(Arrays.asList(1L, 3L));

            orderService.update(order4.getId(), newOrderDtoIn);
        });
    }

    @Test
    public void ThrowResourceNotFoundExceptionIfDeletePizzaIdDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.delete(order4.getId());
        });
    }
}
