package ua.belozorov.lunchvoting.service.lunchplace;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.service.AbstractServiceTest;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;
import static ua.belozorov.lunchvoting.MatcherUtils.matchCollection;
import static ua.belozorov.lunchvoting.MatcherUtils.matchSingle;
import static ua.belozorov.lunchvoting.testdata.LunchPlaceTestData.*;
import static ua.belozorov.lunchvoting.testdata.UserTestData.ADMIN;
import static ua.belozorov.lunchvoting.testdata.UserTestData.ADMIN_ID;
import static ua.belozorov.lunchvoting.testdata.UserTestData.GOD_ID;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 22.11.16.
 */
public class LunchPlaceServiceTest extends AbstractServiceTest {

    @Autowired
    private ILunchPlaceService service;

    @Test
    public void create() throws Exception {
        List<String> phones = Arrays.asList("0661234567", "0441234567");
        LunchPlace expected = new LunchPlace("NEW_PLACE_ID", "New Place", "New Address", "New Description", phones,
                Collections.emptyList(), ADMIN);
        LunchPlace actual = service.create(expected, ADMIN_ID);

//        expected.setId(actual.getId());
//        expected.setAdmin(actual.getAdmin());
        Collections.sort(phones);
//        expected.setPhones(phones);

        assertThat(actual, matchSingle(expected, LUNCH_PLACE_COMPARATOR));
    }

    @Test
    public void update() throws Exception {
        LunchPlace placeToUpdate = service.get(PLACE2_ID, ADMIN_ID);
//        placeToUpdate.setAddress("Updated Address");
//        placeToUpdate.setPhones(Collections.singletonList("0481234567"));
        service.update(placeToUpdate, ADMIN_ID);
        assertThat(
                service.get(PLACE2_ID, ADMIN_ID),
                matchSingle(placeToUpdate, LUNCH_PLACE_COMPARATOR)
        );
    }

    @Test
    public void get() throws Exception {
        LunchPlace actual = service.get(PLACE1_ID, ADMIN_ID);
        assertThat(actual, matchSingle(PLACE1, LUNCH_PLACE_COMPARATOR));
    }

    @Test
    public void getAll() throws Exception {
        Collection<LunchPlace> actual = service.getAll(GOD_ID);
        assertThat(
                actual,
                contains(matchCollection(Arrays.asList(PLACE4, PLACE3), LUNCH_PLACE_COMPARATOR))
        );
    }

    @Test
    public void delete() throws Exception {
        service.delete(PLACE1_ID, ADMIN_ID);
        Collection<LunchPlace> actual = service.getAll(ADMIN_ID);
        assertThat(
                actual,
                contains(matchCollection(Collections.singletonList(PLACE2), LUNCH_PLACE_COMPARATOR))
        );
    }

}