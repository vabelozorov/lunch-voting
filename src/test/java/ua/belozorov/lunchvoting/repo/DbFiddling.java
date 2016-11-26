package ua.belozorov.lunchvoting.repo;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.repository.lunchplace.CrudLunchPlaceRepository;
import ua.belozorov.lunchvoting.service.AbstractServiceTest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static ua.belozorov.lunchvoting.testdata.LunchPlaceTestData.PLACE1_ID;
import static ua.belozorov.lunchvoting.testdata.UserTestData.ADMIN_ID;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 22.11.16.
 */
public class DbFiddling extends AbstractServiceTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private CrudLunchPlaceRepository crudLunchPlaceRepository;
}
