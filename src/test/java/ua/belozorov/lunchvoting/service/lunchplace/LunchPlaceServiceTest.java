package ua.belozorov.lunchvoting.service.lunchplace;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.belozorov.lunchvoting.MatcherUtils;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.repository.lunchplace.LunchPlaceRepository;
import ua.belozorov.lunchvoting.service.AbstractServiceTest;
import ua.belozorov.lunchvoting.to.LunchPlaceTo;
import ua.belozorov.lunchvoting.to.transformers.LunchPlaceTransformer;

import javax.persistence.PersistenceException;
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
        LunchPlaceTo expectedTo = new LunchPlaceTo(null, "New Place", "New Address", "New Description", phones);
        LunchPlaceTo actualTo = service.create(expectedTo, GOD);

        phones = phones.stream().sorted().collect(Collectors.toList());

        expectedTo.setId(actualTo.getId());
        expectedTo.setPhones(phones);
        assertThat(actualTo, MatcherUtils.matchByToString(expectedTo));

        LunchPlace expected = LunchPlace.builder(LunchPlaceTransformer.toEntity(expectedTo, GOD_ID))
                                            .id(actualTo.getId())
                                            .phones(phones)
                                            .build();
        assertThat(
                repository.getWithPhones(actualTo.getId(), GOD_ID),
                matchSingle(expected, LUNCH_PLACE_COMPARATOR)
        );
    }

    @Test
    public void update() throws Exception {
        LunchPlaceTo expectedTo = new LunchPlaceTo(PLACE2_ID, PLACE2.getName(), "Updated Address", PLACE2.getDescription(),
                Collections.singletonList("0481234567"));
        service.update(expectedTo, ADMIN);

        LunchPlace expected = LunchPlaceTransformer.toEntity(expectedTo, ADMIN_ID);

        assertThat(
                repository.getWithPhones(PLACE2_ID, ADMIN_ID),
                matchSingle(expected, LUNCH_PLACE_COMPARATOR)
        );
    }

    @Test
    public void get() throws Exception {
        LunchPlaceTo actual = service.get(PLACE1_ID, ADMIN);
        assertThat(actual, matchByToString(LunchPlaceTransformer.toDto(PLACE1)));
    }

    @Test
    public void getAll() throws Exception {
        Collection<LunchPlaceTo> actual = service.getAll(GOD);
        assertReflectionEquals(
                LunchPlaceTransformer.collectionToDto(Arrays.asList(PLACE4, PLACE3)),
                actual
        );
    }

    @Test
    public void delete() throws Exception {
        service.delete(PLACE1_ID, ADMIN);
        Collection<LunchPlace> actual = repository.getAll(ADMIN_ID);
        assertThat(
                actual,
                contains(matchCollection(Collections.singletonList(PLACE2), LUNCH_PLACE_COMPARATOR))
        );
    }

    @Test(expected = NotFoundException.class)
    public void getNotExisting() {
        service.get("NOT_EXISTING_ID", ADMIN);
    }

    @Test(expected = NotFoundException.class)
    public void updateNotExisting() {
        LunchPlaceTo expectedTo = new LunchPlaceTo("NOT_EXISTING_ID", PLACE2.getName(), "Updated Address", PLACE2.getDescription(),
                Collections.singletonList("0481234567"));
        service.update(expectedTo, ADMIN);
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
        service.create(expectedTo, ADMIN);
    }
}