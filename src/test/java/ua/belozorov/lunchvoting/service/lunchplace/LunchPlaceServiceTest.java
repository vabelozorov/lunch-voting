package ua.belozorov.lunchvoting.service.lunchplace;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import ua.belozorov.lunchvoting.exceptions.DuplicateDataException;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.lunchplace.Menu;
import ua.belozorov.lunchvoting.repository.lunchplace.LunchPlaceRepository;
import ua.belozorov.lunchvoting.repository.lunchplace.MenuRepositoryImpl;
import ua.belozorov.lunchvoting.service.AbstractServiceTest;
import ua.belozorov.lunchvoting.to.LunchPlaceTo;
import ua.belozorov.lunchvoting.to.transformers.DtoIntoEntity;
import ua.belozorov.lunchvoting.util.ExceptionUtils;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorCode;

import javax.persistence.PersistenceException;
import java.time.LocalDate;
import java.util.*;

import static com.vladmihalcea.sql.SQLStatementCountValidator.reset;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;
import static ua.belozorov.lunchvoting.MatcherUtils.*;
import static ua.belozorov.lunchvoting.model.lunchplace.LunchPlaceTestData.*;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 22.11.16.
 */
public class LunchPlaceServiceTest extends AbstractServiceTest {

    @Autowired
    private LunchPlaceService service;

    @Autowired
    private LunchPlaceRepository repository;

    private final String areaId = testAreas.getFirstAreaId();

    @Test
    public void update() throws Exception {
        LunchPlace place2 = testPlaces.getPlace2();

        reset();
        service.bulkUpdate(areaId,  place2.getId(), null, "Updated Address", null, Collections.singleton("0481234567"));
        assertSql(2, 0, 1, 0); //TODO expected 1 selects. Unnecessary select of lazy Menu#lunchplace

        LunchPlace expected = place2.toBuilder().address("Updated Address")
                                .phones(Collections.singleton("0481234567")).build();

        assertThat(
                repository.get(areaId, place2.getId()),
                matchSingle(expected, LUNCH_PLACE_COMPARATOR)
        );
    }

    @Test
    public void get() throws Exception {
        reset();
        LunchPlace actual = service.get(areaId, testPlaces.getPlace4Id());
        assertSelect(1);

        assertThat(actual, matchByToString(testPlaces.getPlace4()));
    }

    @Test(expected = NotFoundException.class)
    public void GetPlaceFailsIfAreaNotCorresponds() throws Exception {
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
        LunchPlaceTo expected = new LunchPlaceTo(testPlaces.getPlace2().getName(), "Updated Address", testPlaces.getPlace2().getDescription(),
                Collections.singleton("0481234567"));
        service.bulkUpdate(areaId, "I_DO_NOT_EXIST", "new name", null, null, null);
    }

    @Test(expected = NotFoundException.class)
    public void deleteNotExistingFails() {
        service.delete(areaId, "NOT_EXISTING_ID");
    }

    @Test(expected = DuplicateDataException.class)
    public void createPlaceWithDuplicateNameFails() {
        LunchPlaceTo expectedTo = new LunchPlaceTo( testPlaces.getPlace1().getName(),
                "New Address", "New Description", Collections.emptySet());

        ExceptionUtils.executeAndUnwrapException(
                () -> service.create(DtoIntoEntity.toLunchPlace(expectedTo, null)),
                DataIntegrityViolationException.class,
                new DuplicateDataException(ErrorCode.DUPLICATE_PLACE_NAME, new Object[]{})
        );
    }

    @Test
    public void getMenuWithoutDishes() throws Exception {
        reset();
        Menu menu = service.getMenu(testAreas.getFirstAreaId(), testPlaces.getPlace4Id(), testPlaces.getMenu1Id());
        assertSelect(2); //TODO expected 1 select

        assertThat(menu, matchSingle(testPlaces.getMenu1(), MENU_COMPARATOR));
    }

    @Test
    public void getMenuWithDishes() throws Exception {
        reset();
        Menu menu = service.getMenu(testAreas.getFirstAreaId(), testPlaces.getPlace4Id(), testPlaces.getMenu1Id(), MenuRepositoryImpl.Fields.DISHES);
        assertSelect(3); //TODO expected 2 selects

        assertThat(menu, matchSingle(testPlaces.getMenu1(), MENU_WITH_DISHES_COMPARATOR));
    }

    @Test
    public void deleteMenu() throws Exception {
        Menu menu = service.getMenu(areaId, testPlaces.getPlace4Id(), testPlaces.getMenu1Id());

        assertNotNull(menu);
        reset();
        service.deleteMenu(areaId, testPlaces.getPlace4Id(), testPlaces.getMenu1Id());
        assertSql(2, 0, 0, 1); //TODO expected 1 select

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