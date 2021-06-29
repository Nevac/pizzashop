package ch.cagatay.pizzashop;

import ch.cagatay.pizzashop.dto.PizzaDto;
import ch.cagatay.pizzashop.model.Pizza;
import ch.cagatay.pizzashop.repository.PizzaRepository;
import io.restassured.RestAssured;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

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

    @Before
    void setUp() {
        RestAssured.port = DEFAULT_PORT;
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
    }

    private void insertPizzas() {
        Pizza pizza1 = new Pizza("Margharita", "Cheese and Tomato", 12.0f, true);
        Pizza pizza2 = new Pizza("Proscuitto", "Pork Salami", 15.0f, true);
        Pizza pizza3 = new Pizza("Funghi", "Mushrooms and Cheese", 15.0f, true);
        Pizza pizza4 = new Pizza("Hawaii", "Pineapples do not belong onto pizzas", 15.0f, false);
        pizzaRepository.saveAllAndFlush(Arrays.asList(pizza1, pizza2, pizza3, pizza4));
    }

    private void insertOnePizza() {
        Pizza pizza1 = new Pizza("Margharita", "Cheese and Tomato", 12.0f, true);
        pizzaRepository.saveAndFlush(pizza1);
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
        Response response = get(String.format("%s/%d", routerPath, 1)).then().extract().response();

        assertEquals(1L, response.jsonPath().getLong("id"));
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

        Response response = putPizza(pizzaDto, 1L);

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

        Response response = putPizza(pizzaDto, 2L);

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

        Response response = putPizza(pizzaDto, 1L);
        assertEquals(422, response.statusCode());
        assertEquals("Unprocessable Body Entity", response.body().asString());

        pizzaDto.setName("");
        response = putPizza(pizzaDto, 1L);
        assertEquals(422, response.statusCode());
        assertEquals("Unprocessable Body Entity", response.body().asString());

        pizzaDto.setName("Padrone");
        pizzaDto.setDescription(null);
        response = putPizza(pizzaDto, 1L);
        assertEquals(422, response.statusCode());
        assertEquals("Unprocessable Body Entity", response.body().asString());

        pizzaDto.setDescription("");
        response = putPizza(pizzaDto, 1L);
        assertEquals(422, response.statusCode());
        assertEquals("Unprocessable Body Entity", response.body().asString());

        pizzaDto.setDescription("Beef");
        pizzaDto.setPrice(null);
        response = putPizza(pizzaDto, 1L);
        assertEquals(422, response.statusCode());
        assertEquals("Unprocessable Body Entity", response.body().asString());

        pizzaDto.setPrice(-1F);
        response = putPizza(pizzaDto, 1L);
        assertEquals(422, response.statusCode());
        assertEquals("Unprocessable Body Entity", response.body().asString());

        pizzaDto.setPrice(17F);
        pizzaDto.setActive(null);
        response = putPizza(pizzaDto, 1L);
        assertEquals(422, response.statusCode());
        assertEquals("Unprocessable Body Entity", response.body().asString());

        pizzaDto.setActive(true);
        response = putPizza(pizzaDto, 1L);
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
        Response response = delete(String.format("%s/%d", routerPath, 1))
                .then().extract().response();

        assertEquals(204, response.statusCode());

        response = get(String.format("%s/%d", routerPath, 1))
                .then().extract().response();

        assertEquals(200, response.statusCode());
        assertEquals(false, response.jsonPath().get("active"));
    }

    @Test
    void ThrowExceptionIfDeleteOnNonExistentPizza() {
        insertOnePizza();
        Response response = delete(String.format("%s/%d", routerPath, 300))
                .then().extract().response();

        assertEquals(404, response.statusCode());
        assertEquals("Pizza Not Found", response.body().asString());
    }
}