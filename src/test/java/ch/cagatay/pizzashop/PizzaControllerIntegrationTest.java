package ch.cagatay.pizzashop;

import ch.cagatay.pizzashop.dto.PizzaDto;
import ch.cagatay.pizzashop.model.Pizza;
import ch.cagatay.pizzashop.repository.PizzaRepository;
import io.restassured.RestAssured;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(
        locations = "classpath:integrationtest.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class PizzaControllerIntegrationTest {

    private final String routerPath = "/pizza";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PizzaRepository pizzaRepository;

    private Pizza pizza1;

    @Before
    void setUp() {
        RestAssured.port = DEFAULT_PORT;
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
    }

    private void insertPizzas() {
        pizza1 = new Pizza("Margharita", "Cheese and Tomato", 12.0f, true);
        Pizza pizza2 = new Pizza("Proscuitto", "Pork Salami", 15.0f, true);
        Pizza pizza3 = new Pizza("Funghi", "Mushrooms and Cheese", 15.0f, true);
        Pizza pizza4 = new Pizza("Hawaii", "Pineapples do not belong onto pizzas", 15.0f, false);
        List<Pizza> pizzas = pizzaRepository.saveAllAndFlush(Arrays.asList(pizza1, pizza2, pizza3, pizza4));
        pizza1 = pizzas.get(0);
    }

    private void insertOnePizza() {
        pizza1 = new Pizza("Margharita", "Cheese and Tomato", 12.0f, true);
        pizza1 = pizzaRepository.saveAndFlush(pizza1);
    }

    @Test
    void GetAllPizzas() {
        insertPizzas();
        Response response = get(routerPath).then().extract().response();

        assertEquals(4, response.jsonPath().getList("").size());
    }

    @Test
    void GetPizza() {
        insertOnePizza();
        Response response = get(String.format("%s/%d", routerPath, pizza1.getId())).then().extract().response();

        assertEquals(pizza1.getId().longValue(), response.jsonPath().getLong("id"));
    }

    @Test
    void ThrowExceptionIfGetOnNonExistentOrder() {
        insertOnePizza();
        Response response = get(String.format("%s/%d", routerPath, 10L)).then().extract().response();

        assertEquals(404, response.statusCode());
        assertEquals("Pizza Not Found", response.body().asString());
    }

    @Test
    @Transactional
    void CreatePizza() {
        PizzaDto pizzaDto = new PizzaDto();
        pizzaDto.setName("Padrone");
        pizzaDto.setDescription("Beef");
        pizzaDto.setPrice(17F);
        pizzaDto.setActive(true);

        Response response = postPizza(pizzaDto);

        assertEquals(201, response.statusCode());
        assertEquals("Padrone", response.jsonPath().getString("name"));
        assertEquals("Beef", response.jsonPath().getString("description"));
        assertEquals(17, response.jsonPath().getFloat("price"), 0.001);
        assertTrue("Pizza not active", response.jsonPath().getBoolean("active"));
    }

    @Test
    void ThrowExceptionIfCreatePizzaWithInvalidBody() {
        PizzaDto pizzaDto = new PizzaDto();
        pizzaDto.setName(null);
        pizzaDto.setDescription("Beef");
        pizzaDto.setPrice(17F);
        pizzaDto.setActive(true);

        Response response = postPizza(pizzaDto);
        assertEquals(422, response.statusCode());
        assertEquals("Unprocessable Body Entity", response.body().asString());

        pizzaDto.setName("");
        response = postPizza(pizzaDto);
        assertEquals(422, response.statusCode());
        assertEquals("Unprocessable Body Entity", response.body().asString());

        pizzaDto.setName("Padrone");
        pizzaDto.setDescription(null);
        response = postPizza(pizzaDto);
        assertEquals(422, response.statusCode());
        assertEquals("Unprocessable Body Entity", response.body().asString());

        pizzaDto.setDescription("");
        response = postPizza(pizzaDto);
        assertEquals(422, response.statusCode());
        assertEquals("Unprocessable Body Entity", response.body().asString());

        pizzaDto.setDescription("Beef");
        pizzaDto.setPrice(null);
        response = postPizza(pizzaDto);
        assertEquals(422, response.statusCode());
        assertEquals("Unprocessable Body Entity", response.body().asString());

        pizzaDto.setPrice(-1F);
        response = postPizza(pizzaDto);
        assertEquals(422, response.statusCode());
        assertEquals("Unprocessable Body Entity", response.body().asString());

        pizzaDto.setPrice(17F);
        pizzaDto.setActive(null);
        response = postPizza(pizzaDto);
        assertEquals(422, response.statusCode());
        assertEquals("Unprocessable Body Entity", response.body().asString());

        pizzaDto.setActive(true);
        response = postPizza(pizzaDto);
        assertEquals(201, response.statusCode());
    }

    private Response postPizza(PizzaDto pizzaDto) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(pizzaDto)
                .when()
                .post(routerPath)
                .then().extract().response();
    }

    @Test
    void UpdatePizza() {
        insertOnePizza();
        PizzaDto pizzaDto = new PizzaDto();
        pizzaDto.setName("Padrone");
        pizzaDto.setDescription("Beef");
        pizzaDto.setPrice(17F);
        pizzaDto.setActive(true);

        Response response = putPizza(pizzaDto, pizza1.getId());

        assertEquals(204, response.statusCode());
    }

    @Test
    void ThrowExceptionIfUpdateOnNonExistentPizza() {
        insertOnePizza();
        PizzaDto pizzaDto = new PizzaDto();
        pizzaDto.setName("Padrone");
        pizzaDto.setDescription("Beef");
        pizzaDto.setPrice(17F);
        pizzaDto.setActive(true);

        Response response = putPizza(pizzaDto, 10L);

        assertEquals(404, response.statusCode());
        assertEquals("Pizza Not Found", response.body().asString());
    }

    @Test
    void ThrowExceptionIfUpdatePizzaWithInvalidBody() {
        insertOnePizza();
        PizzaDto pizzaDto = new PizzaDto();
        pizzaDto.setName(null);
        pizzaDto.setDescription("Beef");
        pizzaDto.setPrice(17F);
        pizzaDto.setActive(true);

        Response response = putPizza(pizzaDto, pizza1.getId());
        assertEquals(422, response.statusCode());
        assertEquals("Unprocessable Body Entity", response.body().asString());

        pizzaDto.setName("");
        response = putPizza(pizzaDto, pizza1.getId());
        assertEquals(422, response.statusCode());
        assertEquals("Unprocessable Body Entity", response.body().asString());

        pizzaDto.setName("Padrone");
        pizzaDto.setDescription(null);
        response = putPizza(pizzaDto, pizza1.getId());
        assertEquals(422, response.statusCode());
        assertEquals("Unprocessable Body Entity", response.body().asString());

        pizzaDto.setDescription("");
        response = putPizza(pizzaDto, pizza1.getId());
        assertEquals(422, response.statusCode());
        assertEquals("Unprocessable Body Entity", response.body().asString());

        pizzaDto.setDescription("Beef");
        pizzaDto.setPrice(null);
        response = putPizza(pizzaDto, pizza1.getId());
        assertEquals(422, response.statusCode());
        assertEquals("Unprocessable Body Entity", response.body().asString());

        pizzaDto.setPrice(-1F);
        response = putPizza(pizzaDto, pizza1.getId());
        assertEquals(422, response.statusCode());
        assertEquals("Unprocessable Body Entity", response.body().asString());

        pizzaDto.setPrice(17F);
        pizzaDto.setActive(null);
        response = putPizza(pizzaDto, pizza1.getId());
        assertEquals(422, response.statusCode());
        assertEquals("Unprocessable Body Entity", response.body().asString());

        pizzaDto.setActive(true);
        response = putPizza(pizzaDto, pizza1.getId());
        assertEquals(204, response.statusCode());
    }

    private Response putPizza(PizzaDto pizzaDto, Long id) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(pizzaDto)
                .when()
                .put(String.format("%s/%d", routerPath, id))
                .then().extract().response();
    }

    @Test
    void DeletePizza() {
        insertOnePizza();
        Response response = delete(String.format("%s/%d", routerPath, pizza1.getId()))
                .then().extract().response();

        assertEquals(204, response.statusCode());

        response = get(String.format("%s/%d", routerPath, pizza1.getId()))
                .then().extract().response();

        assertEquals(200, response.statusCode());
        assertEquals(false, response.jsonPath().get("active"));
    }

    @Test
    void ThrowExceptionIfDeleteOnNonExistentPizza() {
        insertOnePizza();
        Response response = delete(String.format("%s/%d", routerPath, 10L))
                .then().extract().response();

        assertEquals(404, response.statusCode());
        assertEquals("Pizza Not Found", response.body().asString());
    }
}