package ch.cagatay.pizzashop;

import ch.cagatay.pizzashop.dto.OrderDtoOut;
import ch.cagatay.pizzashop.dto.OrderDtoIn;
import ch.cagatay.pizzashop.model.Order;
import ch.cagatay.pizzashop.repository.OrderRepository;
import ch.cagatay.pizzashop.service.OrderService;
import ch.cagatay.pizzashop.service.PizzaService;
import ch.cagatay.pizzashop.service.PizzaShopService;
import ch.cagatay.pizzashop.util.ModelMapper;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {

    @Mock
    ModelMapper mapper;
    @Mock
    PizzaShopService pizzaService;
    @Mock
    OrderRepository orderRepository;
    @InjectMocks
    PizzaShopService orderService;

    private Order order1;
    private Order order2;
    private Order order3;

    private OrderDtoIn orderDtoIn1;
    private OrderDtoIn orderDtoIn2;
    private OrderDtoIn orderDtoIn3;

    private OrderDtoOut orderDtoOut11;
    private OrderDtoOut orderDtoOut12;
    private OrderDtoOut orderDtoOut13;
    
    @Before
    public void setUp() {

        //Generate Order Spies

    }

    /**
     * Generates a spy object which spies on the given order and mocks the getID method.
     * This is necessary, since certain checks perform id comparisons and the id cannot be set from outside.
     * @param id the id the spy should return upon calling getId()
     * @param order the order which should be mocked
     * @return A spy order object which returns the given id upon calling getID()
     */
    private Order generateSpyOrder(long id, Order order){
        Order o = Mockito.spy(order);
        Mockito.when(o.getId()).thenReturn(id);
        return o;
    }
}
