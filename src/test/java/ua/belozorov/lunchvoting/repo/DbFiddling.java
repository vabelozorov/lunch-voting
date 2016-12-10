package ua.belozorov.lunchvoting.repo;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.repository.lunchplace.LunchPlaceRepositoryImpl;
import ua.belozorov.lunchvoting.service.AbstractServiceTest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.Assert.assertTrue;
import static ua.belozorov.lunchvoting.testdata.LunchPlaceTestData.PLACE4_ID;
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
    private LunchPlaceRepositoryImpl.CrudLunchPlaceRepository crudLunchPlaceRepository;

    @Test
    public void testGetWithMenusEntityGraph() {
        LunchPlace place = getWithMenus();
        assertTrue(place.getMenus().size() > 0);
    }

    @Transactional
    private LunchPlace getWithMenus() {
        return crudLunchPlaceRepository.getWithMenus(PLACE4_ID, GOD_ID);
    }
}
