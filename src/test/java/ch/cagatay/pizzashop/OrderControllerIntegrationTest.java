package ch.cagatay.pizzashop;

import ch.cagatay.pizzashop.dto.OrderDtoIn;
import ch.cagatay.pizzashop.dto.PizzaDto;
import ch.cagatay.pizzashop.model.Order;
import ch.cagatay.pizzashop.model.OrderStatus;
import ch.cagatay.pizzashop.model.Pizza;
import ch.cagatay.pizzashop.repository.OrderRepository;
import ch.cagatay.pizzashop.repository.PizzaRepository;
import io.restassured.RestAssured;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.junit.Assert.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(
        locations = "classpath:integrationtest.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class OrderControllerIntegrationTest {

    private final String routerPath = "/order";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PizzaRepository pizzaRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Pizza pizza1;
    private Pizza pizza2;
    private Pizza pizza3;
    private Pizza pizza4;

    private Order order1;

    @Before
    void setUp() {
        RestAssured.port = DEFAULT_PORT;
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
    }

    private void insertOrders() {
        pizza1 = new Pizza("Margharita", "Cheese and Tomato", 12.0f, true);
        pizza2 = new Pizza("Proscuitto", "Pork Salami", 15.0f, true);
        pizza3 = new Pizza("Funghi", "Mushrooms and Cheese", 15.0f, true);
        pizza4 =
                new Pizza("Hawaii", "Pineapples do not belong onto pizzas", 15.0f, false);
        List<Pizza> pizzas = pizzaRepository.saveAllAndFlush(Arrays.asList(pizza1, pizza2, pizza3, pizza4));
        pizza1 = pizzas.get(0);
        pizza2 = pizzas.get(1);
        pizza3 = pizzas.get(2);
        pizza4 = pizzas.get(3);

        order1 = new Order("Address 1", "0791111111", OrderStatus.PENDING,
                Arrays.asList(pizza1, pizza2));
        Order order2 = new Order("Address 2", "0792222222", OrderStatus.IN_PROCESS,
                Arrays.asList(pizza2, pizza3));
        Order order3 = new Order("Address 3", "0793333333", OrderStatus.IN_DELIVERY,
                Arrays.asList(pizza2, pizza3, pizza4));
        List<Order> orders = orderRepository.saveAllAndFlush(Arrays.asList(order1, order2, order3));
        order1 = orders.get(0);
    }

    private void insertOneOrder() {
        pizza1 = new Pizza("Margharita", "Cheese and Tomato", 12.0f, true);
        pizza2 = new Pizza("Proscuitto", "Pork Salami", 15.0f, true);
        List<Pizza> pizzas = pizzaRepository.saveAllAndFlush(Arrays.asList(pizza1, pizza2));
        pizza1 = pizzas.get(0);
        pizza2 = pizzas.get(1);

        order1 = new Order("Adress 1", "0791111111", OrderStatus.PENDING,
                Arrays.asList(pizza1, pizza2));
        order1 = orderRepository.saveAndFlush(order1);
    }

    private void insertPizzas() {
        pizza1 = new Pizza("Margharita", "Cheese and Tomato", 12.0f, true);
        pizza2 = new Pizza("Proscuitto", "Pork Salami", 15.0f, true);
        pizza3 = new Pizza("Funghi", "Mushrooms and Cheese", 15.0f, true);
        pizza4 =
                new Pizza("Hawaii", "Pineapples do not belong onto pizzas", 15.0f, false);
        List<Pizza> pizzas = pizzaRepository.saveAllAndFlush(Arrays.asList(pizza1, pizza2, pizza3, pizza4));
        pizza1 = pizzas.get(0);
        pizza2 = pizzas.get(1);
        pizza3 = pizzas.get(2);
        pizza4 = pizzas.get(3);
    }

    @Test
    void GetAllOrders() {
        insertOrders();
        Response response = get(routerPath).then().extract().response();

        assertEquals(3, response.jsonPath().getList("").size());
    }

    @Test
    void GetOrder() {
        insertOneOrder();
        Response response = get(String.format("%s/%d", routerPath, 3)).then().extract().response();

        assertEquals(order1.getId().longValue(), response.jsonPath().getLong("id"));
    }

    @Test
    void ThrowExceptionIfGetOnNonExistentOrder() {
        insertOneOrder();
        Response response = get(String.format("%s/%d", routerPath, 10L)).then().extract().response();

        assertEquals(404, response.statusCode());
        assertEquals("Order Not Found", response.body().asString());
    }

    @Test
    void CreateOrder() {
        insertPizzas();
        OrderDtoIn orderDtoIn = new OrderDtoIn();
        orderDtoIn.setAddress("Address new");
        orderDtoIn.setPhone("0790000000");
        orderDtoIn.setStatus(OrderStatus.PENDING);
        orderDtoIn.setPizzaIds(Arrays.asList(pizza1.getId(), pizza2.getId(), pizza4.getId()));

        Response response = postOrder(orderDtoIn);

        assertEquals(201, response.statusCode());
        assertEquals("Address new", response.jsonPath().getString("address"));
        assertEquals("0790000000", response.jsonPath().getString("phone"));
        assertEquals("pending", response.jsonPath().getString("status"));
        List<PizzaDto> pizzas = response.jsonPath().getList("pizzas", PizzaDto.class);
        assertEquals(3, pizzas.size());
        assertEquals(pizza1.getId(), pizzas.get(0).getId());
        assertEquals(pizza2.getId(), pizzas.get(1).getId());
        assertEquals(pizza4.getId(), pizzas.get(2).getId());
    }

    @Test
    void ThrowExceptionIfCreateOrderPizzaDoesNotExist() {
        insertPizzas();
        OrderDtoIn orderDtoIn = new OrderDtoIn();
        orderDtoIn.setAddress("Addess new");
        orderDtoIn.setPhone("0790000000");
        orderDtoIn.setStatus(OrderStatus.PENDING);
        orderDtoIn.setPizzaIds(Arrays.asList(pizza1.getId(), 5L));

        Response response = postOrder(orderDtoIn);

        assertEquals(404, response.statusCode());
        assertEquals("Pizza Not Found", response.body().asString());
    }

    @Test
    void ThrowExceptionIfCreateOrderWithInvalidBody() {
        insertPizzas();
        OrderDtoIn orderDtoIn = new OrderDtoIn();
        orderDtoIn.setAddress(null);
        orderDtoIn.setPhone("0790000000");
        orderDtoIn.setStatus(OrderStatus.PENDING);
        orderDtoIn.setPizzaIds(Arrays.asList(pizza1.getId(), pizza2.getId(), pizza4.getId()));

        Response response = postOrder(orderDtoIn);
        assertEquals(422, response.statusCode());
        assertEquals("Unprocessable Body Entity", response.body().asString());

        orderDtoIn.setAddress("");
        response = postOrder(orderDtoIn);
        assertEquals(422, response.statusCode());
        assertEquals("Unprocessable Body Entity", response.body().asString());

        orderDtoIn.setAddress("Address new");
        orderDtoIn.setPhone(null);
        response = postOrder(orderDtoIn);
        assertEquals(422, response.statusCode());
        assertEquals("Unprocessable Body Entity", response.body().asString());

        orderDtoIn.setPhone("");
        response = postOrder(orderDtoIn);
        assertEquals(422, response.statusCode());
        assertEquals("Unprocessable Body Entity", response.body().asString());

        orderDtoIn.setPhone("0790000000");
        orderDtoIn.setStatus(null);
        response = postOrder(orderDtoIn);
        assertEquals(422, response.statusCode());
        assertEquals("Unprocessable Body Entity", response.body().asString());

        orderDtoIn.setStatus(OrderStatus.PENDING);
        orderDtoIn.setPizzaIds(null);
        response = postOrder(orderDtoIn);
        assertEquals(422, response.statusCode());
        assertEquals("Unprocessable Body Entity", response.body().asString());

        orderDtoIn.setPizzaIds(Collections.emptyList());
        response = postOrder(orderDtoIn);
        assertEquals(422, response.statusCode());
        assertEquals("Unprocessable Body Entity", response.body().asString());

        orderDtoIn.setPizzaIds(Arrays.asList(pizza1.getId(),pizza2.getId(), 6L));
        response = postOrder(orderDtoIn);
        assertEquals(404, response.statusCode());
        assertEquals("Pizza Not Found", response.body().asString());

        orderDtoIn.setPizzaIds(Arrays.asList(pizza1.getId(), pizza2.getId(), pizza4.getId()));
        response = postOrder(orderDtoIn);
        assertEquals(201, response.statusCode());
    }

    private Response postOrder(OrderDtoIn orderDtoIn) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(orderDtoIn)
                .when()
                .post(routerPath)
                .then().extract().response();
    }

    @Test
    void UpdateOrder() {
        insertOneOrder();
        OrderDtoIn orderDtoIn = new OrderDtoIn();
        orderDtoIn.setAddress("Address new");
        orderDtoIn.setPhone("0790000000");
        orderDtoIn.setStatus(OrderStatus.PENDING);
        orderDtoIn.setPizzaIds(Arrays.asList(pizza1.getId(), pizza2.getId()));

        Response response = putOrder(orderDtoIn, order1.getId());

        assertEquals(204, response.statusCode());
    }

    @Test
    void ThrowExceptionIfUpdateOnNonExistentOrder() {
        insertOneOrder();
        OrderDtoIn orderDtoIn = new OrderDtoIn();
        orderDtoIn.setAddress("Address new");
        orderDtoIn.setPhone("0790000000");
        orderDtoIn.setStatus(OrderStatus.PENDING);
        orderDtoIn.setPizzaIds(Arrays.asList(pizza1.getId(), pizza2.getId()));

        Response response = putOrder(orderDtoIn, 10L);

        assertEquals(404, response.statusCode());
        assertEquals("Order Not Found", response.body().asString());
    }

    @Test
    void ThrowExceptionIfUpdateOrderPizzaDoesNotExist() {
        insertOneOrder();
        OrderDtoIn orderDtoIn = new OrderDtoIn();
        orderDtoIn.setAddress("Address new");
        orderDtoIn.setPhone("0790000000");
        orderDtoIn.setStatus(OrderStatus.PENDING);
        orderDtoIn.setPizzaIds(Arrays.asList(pizza1.getId(), 5L));

        Response response = putOrder(orderDtoIn, order1.getId());

        assertEquals(404, response.statusCode());
        assertEquals("Pizza Not Found", response.body().asString());
    }

    @Test
    void ThrowExceptionIfUpdateOrderWithInvalidBody() {
        insertOneOrder();
        OrderDtoIn orderDtoIn = new OrderDtoIn();
        orderDtoIn.setAddress(null);
        orderDtoIn.setPhone("0790000000");
        orderDtoIn.setStatus(OrderStatus.PENDING);
        orderDtoIn.setPizzaIds(Arrays.asList(pizza1.getId(), pizza2.getId()));

        Response response = putOrder(orderDtoIn, order1.getId());
        assertEquals(422, response.statusCode());
        assertEquals("Unprocessable Body Entity", response.body().asString());

        orderDtoIn.setAddress("");
        response = putOrder(orderDtoIn, order1.getId());
        assertEquals(422, response.statusCode());
        assertEquals("Unprocessable Body Entity", response.body().asString());

        orderDtoIn.setAddress("Address new");
        orderDtoIn.setPhone(null);
        response = putOrder(orderDtoIn, order1.getId());
        assertEquals(422, response.statusCode());
        assertEquals("Unprocessable Body Entity", response.body().asString());

        orderDtoIn.setPhone("");
        response = putOrder(orderDtoIn, order1.getId());
        assertEquals(422, response.statusCode());
        assertEquals("Unprocessable Body Entity", response.body().asString());

        orderDtoIn.setPhone("0790000000");
        orderDtoIn.setStatus(null);
        response = putOrder(orderDtoIn, order1.getId());
        assertEquals(422, response.statusCode());
        assertEquals("Unprocessable Body Entity", response.body().asString());

        orderDtoIn.setStatus(OrderStatus.PENDING);
        orderDtoIn.setPizzaIds(null);
        response = putOrder(orderDtoIn, order1.getId());
        assertEquals(422, response.statusCode());
        assertEquals("Unprocessable Body Entity", response.body().asString());

        orderDtoIn.setPizzaIds(Collections.emptyList());
        response = putOrder(orderDtoIn, order1.getId());
        assertEquals(422, response.statusCode());
        assertEquals("Unprocessable Body Entity", response.body().asString());

        orderDtoIn.setPizzaIds(Arrays.asList(pizza1.getId(), pizza2.getId()));
        response = putOrder(orderDtoIn, order1.getId());
        assertEquals(204, response.statusCode());
    }

    private Response putOrder(OrderDtoIn orderDtoIn, Long id) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(orderDtoIn)
                .when()
                .put(String.format("%s/%d", routerPath, id))
                .then().extract().response();
    }

    @Test
    void DeleteOrder() {
        insertOneOrder();
        Response response = delete(String.format("%s/%d", routerPath, order1.getId()))
                .then().extract().response();

        assertEquals(204, response.statusCode());
    }

    @Test
    void ThrowExceptionIfDeleteOnNonExistentOrder() {
        insertOneOrder();
        Response response = delete(String.format("%s/%d", routerPath, 10L))
                .then().extract().response();

        assertEquals(404, response.statusCode());
        assertEquals("Order Not Found", response.body().asString());
    }
}