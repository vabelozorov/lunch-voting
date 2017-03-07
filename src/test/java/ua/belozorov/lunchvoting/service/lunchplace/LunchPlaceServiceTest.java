package ua.belozorov.lunchvoting.service.lunchplace;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.belozorov.lunchvoting.web.exceptionhandling.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.lunchplace.Menu;
import ua.belozorov.lunchvoting.repository.lunchplace.LunchPlaceRepository;
import ua.belozorov.lunchvoting.repository.lunchplace.MenuRepository;
import ua.belozorov.lunchvoting.repository.lunchplace.MenuRepositoryImpl;
import ua.belozorov.lunchvoting.service.AbstractServiceTest;

import java.time.LocalDate;
import java.util.*;

import static com.vladmihalcea.sql.SQLStatementCountValidator.reset;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;
import static ua.belozorov.lunchvoting.matching.MatcherUtils.*;
import static ua.belozorov.lunchvoting.model.lunchplace.LunchPlaceTestData.*;

/**

 *
 * Created on 22.11.16.
 */
public class LunchPlaceServiceTest extends AbstractServiceTest {

    @Autowired
    private LunchPlaceService service;

    @Autowired
    private LunchPlaceRepository placeRepository;

    @Autowired
    private MenuRepository menuRepository;

    private final String areaId = testAreas.getFirstAreaId();

    @Test
    public void createLunchPlace() throws Exception {
        Set<String> phones = ImmutableSet.of("0935977663", "0445793264", "0672569486");
        LunchPlace expected = service.create(new LunchPlace(null, "NEW NAME", "", "", phones));
        LunchPlace actual = placeRepository.get(expected.getId());

        assertThat(actual, matchSingle(expected, LUNCH_PLACE_COMPARATOR));
    }

    @Test
    public void update() throws Exception {
        LunchPlace place2 = testPlaces.getPlace2();

        reset();
        service.bulkUpdate(areaId,  place2.getId(), null, "Updated Address", null, Collections.singleton("0481234567"));
        assertSql(1, 0, 1, 0);

        LunchPlace expected = place2.withAddress("Updated Address")
                                .withPhones(Collections.singleton("0481234567"));

        assertThat(
                placeRepository.get(areaId, place2.getId()),
                matchSingle(expected, LUNCH_PLACE_COMPARATOR)
        );
    }

    @Test
    public void get() throws Exception {
        reset();
        LunchPlace actual = service.get(areaId, testPlaces.getPlace4Id());
        assertSelect(1);

        assertThat(actual, matchSingle(testPlaces.getPlace4(), LUNCH_PLACE_COMPARATOR));
    }

    @Test(expected = NotFoundException.class)
    public void getPlaceFailsIfAreaNotCorresponds() throws Exception {
        LunchPlace actual = service.get(testAreas.getSecondAreaId(), testPlaces.getPlace4Id());
    }

    @Test
    public void getAll() throws Exception {
        reset();
        Collection<LunchPlace> actual = service.getAll(areaId);
        assertSelect(1);

        assertThat(
                actual,
                contains(matchCollection(testPlaces.getA1Places(), LUNCH_PLACE_COMPARATOR))
        );
    }

    @Test
    public void testGetMultipleNoIds() throws Exception {
        reset();
        Collection<LunchPlace> actual = service.getMultiple(areaId, Collections.emptySet());
        assertSelect(1);

        assertThat(
                actual,
                contains(matchCollection(testPlaces.getA1Places(), LUNCH_PLACE_COMPARATOR))
        );
    }

    @Test
    public void testGetMultipleWhenIdsProvided() throws Exception {
        reset();
        Collection<LunchPlace> actual = service.getMultiple(areaId,
                ImmutableSet.of(testPlaces.getPlace3Id(), testPlaces.getPlace4Id()));
        assertSelect(1);

        assertThat(
                actual,
                contains(matchCollection(Arrays.asList(testPlaces.getPlace4(), testPlaces.getPlace3()), LUNCH_PLACE_COMPARATOR))
        );
    }

    @Test(expected = NotFoundException.class)
    public void GetMultipleWhenHasNotExistingIdsFails() throws Exception {
        service.getMultiple(areaId, ImmutableSet.of(testPlaces.getPlace3Id(), "NOT_EXISTS_ID"));
    }

    @Test
    public void testGetMultiplePlacesWithMenu() throws Exception {
        LocalDate now = LocalDate.now();

        reset();
        Collection<LunchPlace> withMenu = service.getMultipleWithMenu(areaId, Sets.newHashSet(testPlaces.getPlace3Id(), testPlaces.getPlace4Id()), now, now);
        assertSelect(1);

        assertTrue(withMenu.stream().mapToLong(lp -> lp.getMenus().size()).sum() == 3);
    }

    @Test
    public void deletePlace() throws Exception {
        int initialSize = service.getAll(areaId).size();

        reset();
        service.delete(areaId, testPlaces.getPlace1Id());
        assertDelete(1);

        Collection<LunchPlace> all = service.getAll(areaId);
        assertTrue(all.size() == initialSize - 1 && all.stream().noneMatch(place -> place.getId().equals(testPlaces.getPlace1Id())));
    }

    @Test(expected = NotFoundException.class)
    public void getNotExistingFails() {
        service.get(areaId, "NOT_EXISTING_ID");
    }

    @Test(expected = NotFoundException.class)
    public void updateNotExistingFails() {
        service.bulkUpdate(areaId, "I_DO_NOT_EXIST", "new name", null, null, null);
    }

    @Test(expected = NotFoundException.class)
    public void deleteNotExistingFails() {
        service.delete(areaId, "NOT_EXISTING_ID");
    }

    @Test
    public void getMenuWithoutDishes() throws Exception {
        reset();
        Menu menu = service.getMenu(testAreas.getFirstAreaId(), testPlaces.getPlace4Id(), testPlaces.getMenu1Id());
        assertSelect(1);

        assertThat(menu, matchSingle(testPlaces.getMenu1(), MENU_COMPARATOR));
    }

    @Test
    public void getMenuWithDishes() throws Exception {
        reset();
        Menu menu = service.getMenu(testAreas.getFirstAreaId(), testPlaces.getPlace4Id(), testPlaces.getMenu1Id(), MenuRepositoryImpl.Fields.DISHES);
        assertSelect(2);

        assertThat(menu, matchSingle(testPlaces.getMenu1(), MENU_WITH_DISHES_COMPARATOR));
    }

    @Test
    public void deleteMenu() throws Exception {
        Menu menu = menuRepository.get(areaId, testPlaces.getPlace4Id(), testPlaces.getMenu1Id());

        assertNotNull(menu);
        reset();
        service.deleteMenu(areaId, testPlaces.getPlace4Id(), testPlaces.getMenu1Id());
        assertSql(1, 0, 0, 1);

        thrown.expect(NotFoundException.class);
        service.getMenu(areaId, testPlaces.getPlace4Id(), testPlaces.getMenu1Id());
    }

    @Test(expected = NotFoundException.class)
    public void failsDeleteMenuIfAreaNotCorresponds() throws Exception {
        service.deleteMenu(testAreas.getSecondAreaId(), testPlaces.getPlace4Id(), testPlaces.getMenu1Id());
    }

    @Test(expected = NotFoundException.class)
    public void failsGetMenuIfAreaNotCorresponds() throws Exception {
        Menu menu = service.getMenu(testAreas.getSecondAreaId(), testPlaces.getPlace4Id(), testPlaces.getMenu1Id());
    }
}