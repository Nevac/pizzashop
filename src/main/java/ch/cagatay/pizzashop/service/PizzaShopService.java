package ch.cagatay.pizzashop.service;

import ch.cagatay.pizzashop.exception.ResourceNotFoundException;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface PizzaShopService<T, I, O> {
    List<O> getAll(Specification<T> query);
    List<T> getAllByIds(List<Long> ids) throws ResourceNotFoundException;
    O get(Long id) throws ResourceNotFoundException;
    O create(I newObject) throws ResourceNotFoundException;
    void update(Long id, I newObject) throws ResourceNotFoundException;
    void delete(Long id) throws ResourceNotFoundException;
}
