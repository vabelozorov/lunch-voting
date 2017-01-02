package ua.belozorov.lunchvoting.service.lunchplace;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.belozorov.lunchvoting.AuthorizedUser;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.repository.lunchplace.LunchPlaceRepository;
import ua.belozorov.lunchvoting.service.AbstractServiceTest;
import ua.belozorov.lunchvoting.to.LunchPlaceTo;
import ua.belozorov.lunchvoting.to.transformers.DtoIntoEntity;

import javax.persistence.PersistenceException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.junit.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
import static ua.belozorov.lunchvoting.MatcherUtils.matchByToString;
import static ua.belozorov.lunchvoting.MatcherUtils.matchCollection;
import static ua.belozorov.lunchvoting.MatcherUtils.matchSingle;
import static ua.belozorov.lunchvoting.testdata.LunchPlaceTestData.*;
import static ua.belozorov.lunchvoting.testdata.UserTestData.*;

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

    @Test
    public void create() throws Exception {
        List<String> phones = Arrays.asList("0661234567", "0441234567", "0123456789", "1234567890");
        LunchPlaceTo to = new LunchPlaceTo(null, "New Place", "New Address", "New Description", phones);
        String id = service.create(DtoIntoEntity.toLunchPlace(to, GOD_ID), GOD);

        phones = phones.stream().sorted().collect(Collectors.toList());

        LunchPlace actual = service.get(id, GOD);

        LunchPlace expected = LunchPlace.builder()
                .id(id).name(to.getName()).address(to.getAddress()).description(to.getDescription()).phones(phones).adminId(GOD_ID)
                .build();

        assertThat(
                actual,
                matchSingle(expected, LUNCH_PLACE_COMPARATOR)
        );
    }

    @Test
    public void update() throws Exception {
        LunchPlaceTo to = new LunchPlaceTo(PLACE2_ID, PLACE2.getName(), "Updated Address", PLACE2.getDescription(),
                Collections.singletonList("0481234567"));
        service.update(DtoIntoEntity.toLunchPlace(to, ADMIN_ID), ADMIN);

        LunchPlace expected = DtoIntoEntity.toLunchPlace(to, ADMIN_ID);

        assertThat(
                repository.get(PLACE2_ID, ADMIN_ID),
                matchSingle(expected, LUNCH_PLACE_COMPARATOR)
        );
    }

    @Test
    public void get() throws Exception {
        LunchPlace actual = service.get(PLACE4_ID, GOD);
        assertThat(actual, matchByToString(PLACE4));
    }

    @Test
    public void getAll() throws Exception {
        Collection<LunchPlace> actual = service.getAll(GOD);
        assertThat(
                actual,
                contains(matchCollection(Arrays.asList(PLACE4, PLACE3), LUNCH_PLACE_COMPARATOR))
        );
    }

    @Test
    public void testGetMultipleNoIds() throws Exception {
        Collection<LunchPlace> actual = service.getMultiple(Collections.emptyList(), GOD);
        assertThat(
                actual,
                contains(matchCollection(Arrays.asList(PLACE4, PLACE3), LUNCH_PLACE_COMPARATOR))
        );
    }

    @Test
    public void testGetMultiple() throws Exception {
        Collection<LunchPlace> actual = service.getMultiple(Arrays.asList(PLACE3_ID, PLACE4_ID), GOD);
        assertThat(
                actual,
                contains(matchCollection(Arrays.asList(PLACE4, PLACE3), LUNCH_PLACE_COMPARATOR))
        );
    }

    @Test(expected = NotFoundException.class)
    public void testGetMultipleExceptionOnNotExistingIds() throws Exception {
        service.getMultiple(Arrays.asList(PLACE3_ID, "NOT_EXISTS_ID"), GOD);
    }

    @Test
    public void testGetMultipleWithMenu() throws Exception {
        LocalDate now = LocalDate.now();
        Collection<LunchPlace> withMenu = service.getMultipleWithMenu(Arrays.asList(PLACE3_ID, PLACE4_ID), now, now, AuthorizedUser.get());
        assertTrue(withMenu.stream().mapToLong(lp -> lp.getMenus().size()).sum() == 3);
    }

    @Test
    public void delete() throws Exception {
        int initialSize = service.getAll(ADMIN).size();
        service.delete(PLACE1_ID, ADMIN);
        Collection<LunchPlace> all = service.getAll(ADMIN);
        assertTrue(all.size() == initialSize - 1 && all.stream().noneMatch(place -> place.getId().equals(PLACE1_ID)));
    }

    @Test(expected = NotFoundException.class)
    public void getNotExisting() {
        service.get("NOT_EXISTING_ID", ADMIN);
    }

    @Test(expected = NotFoundException.class)
    public void updateNotExisting() {
        LunchPlaceTo expected = new LunchPlaceTo("NOT_EXISTING_ID", PLACE2.getName(), "Updated Address", PLACE2.getDescription(),
                Collections.singletonList("0481234567"));
        service.update(DtoIntoEntity.toLunchPlace(expected, ADMIN_ID), ADMIN);
    }

    @Test(expected = NotFoundException.class)
    public void deleteNotExisting() {
        service.delete("NOT_EXISTING_ID", ADMIN);
    }

    @Test
    public void createDuplicate() {
        List<String> phones = Arrays.asList("0661234567", "0441234567");
        LunchPlaceTo expectedTo = new LunchPlaceTo(PLACE1_ID, "New Place", "New Address", "New Description", phones);

        thrown.expect(PersistenceException.class);
        thrown.expectCause(isA(ConstraintViolationException.class));
        service.create(DtoIntoEntity.toLunchPlace(expectedTo,ADMIN_ID), ADMIN);
    }
}