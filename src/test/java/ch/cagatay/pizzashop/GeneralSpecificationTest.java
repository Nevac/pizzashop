package ch.cagatay.pizzashop;

import ch.cagatay.pizzashop.model.Pizza;
import ch.cagatay.pizzashop.repository.PizzaRepository;
import ch.cagatay.pizzashop.specifications.GeneralSpecification;
import ch.cagatay.pizzashop.specifications.SearchCriteria;
import ch.cagatay.pizzashop.specifications.SearchOperation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThrows;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class GeneralSpecificationTest {

    @Autowired
    private PizzaRepository pizzaRepository;

    private Pizza pizza1;
    private Pizza pizza2;
    private Pizza pizza3;
    private Pizza pizza4;

    @Before
    public void setUp() {
        pizza1 = new Pizza("Margharita", "Cheese and Tomato", 12.0f, true);
        pizza2 = new Pizza("Proscuitto", "Pork Salami", 15.0f, false);
        pizza3 = new Pizza("Funghi", "Mushrooms and Cheese", 15.0f, true);
        pizza4 = new Pizza("Margharita Small", "Cheese and Tomato", 12.0f, true);

        pizzaRepository.saveAll(Arrays.asList(pizza1, pizza2, pizza3, pizza4));
    }

    @Test
    public void ReturnsEntitiesIfEqual() {
        Specification<Pizza> spec =
                new GeneralSpecification<>(new SearchCriteria("name", SearchOperation.EQUALITY, "Margharita"));

        List<Pizza> results = pizzaRepository.findAll(spec);

        MatcherAssert.assertThat(results, hasItem(pizza1));
        MatcherAssert.assertThat(results, not(hasItem(pizza2)));
        MatcherAssert.assertThat(results, not(hasItem(pizza3)));
        MatcherAssert.assertThat(results, not(hasItem(pizza4)));

        spec =
                new GeneralSpecification<>(
                        new SearchCriteria("description", SearchOperation.EQUALITY, "Cheese and Tomato"));

        results = pizzaRepository.findAll(spec);

        MatcherAssert.assertThat(results, hasItem(pizza1));
        MatcherAssert.assertThat(results, not(hasItem(pizza2)));
        MatcherAssert.assertThat(results, not(hasItem(pizza3)));
        MatcherAssert.assertThat(results, hasItem(pizza4));
    }

    @Test
    public void ReturnsEntitiesIfEqualNameAndDescription() {
        Specification<Pizza> spec1 =
                new GeneralSpecification<>(
                        new SearchCriteria("name", SearchOperation.EQUALITY, "Margharita"));
        Specification<Pizza> spec2 =
                new GeneralSpecification<>(
                        new SearchCriteria("description", SearchOperation.EQUALITY, "Cheese and Tomato"));

        List<Pizza> results = pizzaRepository.findAll(Specification.where(spec1).and(spec2));

        MatcherAssert.assertThat(results, hasItem(pizza1));
        MatcherAssert.assertThat(results, not(hasItem(pizza2)));
        MatcherAssert.assertThat(results, not(hasItem(pizza3)));
        MatcherAssert.assertThat(results, not(hasItem(pizza4)));
    }

    @Test
    public void ReturnEntitiesIfEqualNameOrName() {
        Specification<Pizza> spec1 =
                new GeneralSpecification<>(
                        new SearchCriteria("name", SearchOperation.EQUALITY, "Margharita"));
        Specification<Pizza> spec2 =
                new GeneralSpecification<>(
                        new SearchCriteria("name", SearchOperation.EQUALITY, "Proscuitto"));

        List<Pizza> results = pizzaRepository.findAll(Specification.where(spec1).or(spec2));

        MatcherAssert.assertThat(results, hasItem(pizza1));
        MatcherAssert.assertThat(results, hasItem(pizza2));
        MatcherAssert.assertThat(results, not(hasItem(pizza3)));
        MatcherAssert.assertThat(results, not(hasItem(pizza4)));
    }

    @Test
    public void ReturnsEntitiesIfPriceGreaterThan13() {
        Specification<Pizza> spec =
                new GeneralSpecification<>(
                        new SearchCriteria("price", SearchOperation.GREATER_THAN, 13.0));

        List<Pizza> results = pizzaRepository.findAll(spec);

        MatcherAssert.assertThat(results, not(hasItem(pizza1)));
        MatcherAssert.assertThat(results, hasItem(pizza2));
        MatcherAssert.assertThat(results, hasItem(pizza3));
        MatcherAssert.assertThat(results, not(hasItem(pizza4)));
    }

    @Test
    public void ReturnsEntitiesIfPriceLessThan13() {
        Specification<Pizza> spec =
                new GeneralSpecification<>(
                        new SearchCriteria("price", SearchOperation.LESS_THAN, 13.0));

        List<Pizza> results = pizzaRepository.findAll(spec);

        MatcherAssert.assertThat(results, hasItem(pizza1));
        MatcherAssert.assertThat(results, not(hasItem(pizza2)));
        MatcherAssert.assertThat(results, not(hasItem(pizza3)));
        MatcherAssert.assertThat(results, hasItem(pizza4));
    }

    @Test
    public void ReturnsEntitiesIfActiveIsTrue() {
        Specification<Pizza> spec =
                new GeneralSpecification<>(new SearchCriteria("active", SearchOperation.EQUALITY, true));

        List<Pizza> results = pizzaRepository.findAll(spec);

        MatcherAssert.assertThat(results, hasItem(pizza1));
        MatcherAssert.assertThat(results, not(hasItem(pizza2)));
        MatcherAssert.assertThat(results, hasItem(pizza3));
        MatcherAssert.assertThat(results, hasItem(pizza4));
    }

    @Test
    public void ReturnsEntitiesIfActiveIsFalse() {
        Specification<Pizza> spec =
                new GeneralSpecification<>(new SearchCriteria("active", SearchOperation.EQUALITY, false));

        List<Pizza> results = pizzaRepository.findAll(spec);

        MatcherAssert.assertThat(results, not(hasItem(pizza1)));
        MatcherAssert.assertThat(results, hasItem(pizza2));
        MatcherAssert.assertThat(results, not(hasItem(pizza3)));
        MatcherAssert.assertThat(results, not(hasItem(pizza4)));
    }

    @Test
    public void ThrowExceptionIfSpecificationIsWrong() {
        assertThrows(NullPointerException.class, () -> {
            Specification<Pizza> spec = new GeneralSpecification<>(null);
            pizzaRepository.findAll(spec);
        });

        assertThrows(NullPointerException.class, () -> {
            Specification<Pizza> spec = new GeneralSpecification<>(new SearchCriteria(null, null, null));
            pizzaRepository.findAll(spec);
        });

        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            Specification<Pizza> spec = new GeneralSpecification<>(new SearchCriteria("", SearchOperation.EQUALITY, ""));
            pizzaRepository.findAll(spec);
        });

        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            Specification<Pizza> spec = new GeneralSpecification<>(
                    new SearchCriteria("notInPizza", SearchOperation.EQUALITY, ""));
            pizzaRepository.findAll(spec);
        });
    }
}
