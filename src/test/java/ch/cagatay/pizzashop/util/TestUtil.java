package ch.cagatay.pizzashop.util;

import ch.cagatay.pizzashop.model.Pizza;
import org.mockito.Mockito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.Optional;

public class TestUtil {

    /**
     * Sets up a mocked repository by preparing the findAll() and the findById methods.
     * @param repository the mock repository which needs to be set up
     * @param entities the entities which should be contained within the repository
     * @param <T> The type contained within the repository
     */
    @SafeVarargs
    public static <T>void doMockRepoSetup(JpaRepository<T, Long> repository, T... entities){
        Mockito.when(repository.findAll()).thenReturn(Arrays.asList(entities));
        //The id's start with 1 and are then gradually counted upwards in the order that they were passed via the parameters.
        long count = 1;
        for(T entity : entities){
            Mockito.when(repository.findById(count++)).thenReturn(Optional.of(entity));
            Mockito.when(repository.save(entity)).thenReturn(entity);
        }
    }
}