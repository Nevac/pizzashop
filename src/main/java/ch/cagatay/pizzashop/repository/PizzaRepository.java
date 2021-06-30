package ch.cagatay.pizzashop.repository;

import ch.cagatay.pizzashop.model.Pizza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PizzaRepository extends JpaRepository<Pizza, Long>, JpaSpecificationExecutor<Pizza> {
}
