package ua.belozorov.lunchvoting.repo;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.repository.lunchplace.LunchPlaceRepository;
import ua.belozorov.lunchvoting.repository.lunchplace.LunchPlaceRepositoryImpl;
import ua.belozorov.lunchvoting.service.AbstractServiceTest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertTrue;
import static ua.belozorov.lunchvoting.testdata.LunchPlaceTestData.*;
import static ua.belozorov.lunchvoting.testdata.UserTestData.GOD_ID;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 22.11.16.
 */
public class DbFiddling extends AbstractServiceTest {

    @PersistenceContext(name = "AppPersistentUnit")
    private EntityManager em;

    @Autowired
    private LunchPlaceRepository repository;

    @Test
    public void testWithMenus() throws Exception {
        Collection<LunchPlace> places = get();
        System.out.println();
    }

    @Transactional
    private Collection<LunchPlace> get() {
        return repository.getWithMenu(Arrays.asList(), LocalDate.now(), LocalDate.now());
//        return repository.getMultipleWithMenu(Arrays.asList(PLACE1_ID, PLACE4_ID), LocalDate.now(), LocalDate.now());
    }
}
