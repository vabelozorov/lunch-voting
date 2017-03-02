package ua.belozorov.lunchvoting.service.area;

import com.google.common.collect.ImmutableSet;
import org.hibernate.LazyInitializationException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import ua.belozorov.lunchvoting.exceptions.DuplicateDataException;
import ua.belozorov.lunchvoting.exceptions.NoAreaAdminException;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.UserRole;
import ua.belozorov.lunchvoting.model.lunchplace.AreaTestData;
import ua.belozorov.lunchvoting.model.lunchplace.EatingArea;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;
import ua.belozorov.lunchvoting.repository.lunchplace.EatingAreaRepositoryImpl;
import ua.belozorov.lunchvoting.service.AbstractServiceTest;
import ua.belozorov.lunchvoting.service.lunchplace.LunchPlaceService;
import ua.belozorov.lunchvoting.service.user.UserProfileService;
import ua.belozorov.lunchvoting.service.user.UserService;
import ua.belozorov.lunchvoting.service.voting.PollService;
import ua.belozorov.lunchvoting.to.AreaTo;
import ua.belozorov.lunchvoting.util.ExceptionUtils;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorCode;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.vladmihalcea.sql.SQLStatementCountValidator.reset;
import static org.junit.Assert.*;
import static ua.belozorov.lunchvoting.MatcherUtils.*;
import static ua.belozorov.lunchvoting.model.UserTestData.*;
import static ua.belozorov.lunchvoting.model.lunchplace.AreaTestData.AREA_COMPARATOR;
import static ua.belozorov.lunchvoting.model.lunchplace.AreaTestData.AREA_TO_COMPARATOR;
import static ua.belozorov.lunchvoting.model.lunchplace.LunchPlaceTestData.LUNCH_PLACE_COMPARATOR;
import static ua.belozorov.lunchvoting.model.voting.polling.PollTestData.POLL_COMPARATOR;

/**

 *
 * Created on 09.02.17.
 */
public class EatingAreaServiceTest extends AbstractServiceTest {

    @Autowired
    private EatingAreaService areaService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserProfileService profileService;

    @Autowired
    private LunchPlaceService placeService;

    @Autowired
    private PollService pollService;

    private final String areaId = testAreas.getFirstAreaId();

    @Test
    public void createArea() throws Exception {
        User creator = profileService.get(ALIEN_USER1.getId());

        reset();
        EatingArea expected = areaService.create("ChowChow", creator);
        assertSql(3, 1,3, 0);

        EatingArea actual = areaService.getArea(expected.getId());

        assertThat(actual, matchSingle(expected, AREA_COMPARATOR));
        assertTrue(profileService.get(ALIEN_USER1.getId()).getRoles().contains(UserRole.ADMIN));
    }

    @Test(expected = NoAreaAdminException.class)
    public void theOnlyAdminInAreaCannotCreateNewArea() throws Exception {
        userService.setRoles(areaId, ADMIN_ID, Collections.singleton(UserRole.VOTER));;
        areaService.create("NEW_AWESOME_SO_MUCH_BETTER_AREA", GOD);
    }

    @Test(expected = DuplicateDataException.class)
    public void areaNameIsUnique() throws Exception {
        User creator = profileService.get(GOD_ID);
        ExceptionUtils.executeAndUnwrapException(
                () -> areaService.create(testAreas.getFirstAreaName(), creator),
                ConstraintViolationException.class,
                new DuplicateDataException(ErrorCode.AREA_DUPLICATE_NAME, new Object[]{testAreas.getFirstAreaName()})
        );
    }

    @Test
    public void updateAreaName() throws Exception {
        reset();
        areaService.updateAreaName("NEW_AWESOME_NAME", GOD);
        assertSql(1, 0, 1, 0);

        EatingArea actual = areaService.getArea(GOD.getAreaId());

        assertThat(
                actual,
                matchSingle(testAreas.getFirstArea().withName("NEW_AWESOME_NAME"), AREA_COMPARATOR)
        );
    }

    @Test
    public void createUserInArea() throws Exception {
        User newUser = new User("New User", "new@email.com", "strongPassword");

        reset();
        User created = areaService.createUserInArea(areaId, newUser);
        assertSql(3, 1, 2, 0);

        assertThat(
                userService.get(areaId, created.getId()),
                matchSingle(created.assignAreaId(areaId), USER_COMPARATOR)
        );

        //TODO Cannot get EatingArea#users collection without transaction (no Session), fuck knows why
        TransactionStatus transactionStatus = ptm.getTransaction(new DefaultTransactionDefinition());
        EatingArea area = areaService
                .getArea(areaId, EatingAreaRepositoryImpl.Fields.USERS);

        assertTrue(area.getVoters().contains(created));
        ptm.commit(transactionStatus);
    }

    @Test
    public void createPlaceInArea() throws Exception {
        Set<String> phones = ImmutableSet.of("0661234567", "0441234567", "0123456789", "1234567890");

        reset();
        LunchPlace created = areaService.createPlaceInArea(testAreas.getFirstAreaId(),
                "NEW_ID", "NEW_PLACE_NAME", "new address", "new description", phones);
        assertSql(2, 1, 2, 0);

        phones = phones.stream().sorted().collect(Collectors.toSet());
        LunchPlace actual = placeService.get(areaId, created.getId());
        LunchPlace expected = created.withPhones(phones);

        assertThat(
                actual,
                matchSingle(expected, LUNCH_PLACE_COMPARATOR)
        );
    }

    @Test
    public void createPlaceInAnotherAreaWithDuplicateName() throws Exception {
        Set<String> phones = ImmutableSet.of("0661234567", "0441234567", "0123456789", "1234567890");

        areaService.createPlaceInArea(testAreas.getFirstAreaId(),
                null, "NEW_PLACE_NAME", "new address", "new description", phones);
        areaService.createPlaceInArea(testAreas.getSecondAreaId(),
                null, "NEW_PLACE_NAME", "new address", "new description", phones);
    }

    @Test(expected = DuplicateDataException.class)
    public void createPlaceInAreaWithDuplicateNameFails() throws Exception {
        Set<String> phones = ImmutableSet.of("0661234567", "0441234567", "0123456789", "1234567890");
        areaService.createPlaceInArea(testAreas.getFirstAreaId(),
                null, "NEW_PLACE_NAME", "new address", "new description", phones);
        ExceptionUtils.executeAndUnwrapException(
                () -> areaService.createPlaceInArea(testAreas.getFirstAreaId(),
                        null, "NEW_PLACE_NAME", "new address", "new description", phones),
                DataIntegrityViolationException.class,
                new DuplicateDataException(ErrorCode.DUPLICATE_PLACE_NAME, new Object[]{})
        );
    }

    @Test
    public void createPollInAreaForMenuDate() throws Exception {
        reset();
        LunchPlacePoll expected = areaService.createPollInArea(areaId, NOW_DATE.plusDays(2), null, null, null);
        assertSql(3, 3, 4, 0); //TODO expected 2 update; consider changing LunchPlacePoll#pollItems to Set?

        LunchPlacePoll actual = pollService.getWithPollItems(areaId, expected.getId());

        assertThat(actual, matchSingle(expected, POLL_COMPARATOR.noVotes()));
    }

    @Test
    public void getAreaBasicFieldsOnly() throws Exception {
        reset();
        EatingArea actual = areaService.getArea(areaId);
        assertSelect(1);

        assertExceptionCount(LazyInitializationException.class,
                () -> actual.getVoters().size(),
                () -> actual.getPolls().size(),
                () -> actual.getPlaces().size()
        );
    }

    @Test
    public void getAreaWithUsers() throws Exception {
        EatingArea expected = testAreas.getFirstArea();

        reset();
        TransactionStatus transactionStatus = ptm.getTransaction(new DefaultTransactionDefinition());
        EatingArea actual = areaService
                .getArea(expected.getId(), EatingAreaRepositoryImpl.Fields.USERS);
        ptm.commit(transactionStatus);
        assertSelect(2);

        assertThat(expected, matchSingle(actual, AREA_COMPARATOR));
        assertTrue(actual.getVoters().size() == 7);
        assertExceptionCount(LazyInitializationException.class,
                () -> actual.getPolls().size(),
                () -> actual.getPlaces().size()
        );
    }

    @Test
    public void getAreaWithUsersAndPolls() throws Exception {
        EatingArea expected = testAreas.getFirstArea();

        reset();
        TransactionStatus transactionStatus = ptm.getTransaction(new DefaultTransactionDefinition());
        EatingArea actual = areaService.getArea(expected.getId(), EatingAreaRepositoryImpl.Fields.USERS, EatingAreaRepositoryImpl.Fields.POLLS);
        ptm.commit(transactionStatus);
        assertSelect(3);

        assertThat(expected, matchSingle(actual, AREA_COMPARATOR));
        assertTrue(actual.getVoters().size() == 7);
        assertTrue(actual.getPolls().size() == 4);
        assertExceptionCount(LazyInitializationException.class,
                () -> actual.getPlaces().size()
        );
    }

    @Test
    public void getAreaAsToNoSummary() throws Exception {
        AreaTo actual = areaService.getAsTo(testAreas.getFirstAreaId(), false);
        AreaTo expected = AreaTestData.dto(testAreas.getFirstArea());
        assertThat(actual, matchByToString(expected));
    }

    @Test
    public void getAreaAsToWithSummary() throws Exception {
        AreaTo actual = areaService.getAsTo(testAreas.getFirstAreaId(), true);
        AreaTo expected = AreaTestData.dtoSummary(testAreas.getFirstArea());
        assertThat(actual, matchSingle(expected, AREA_TO_COMPARATOR));
    }

    @Test
    public void onAreaDeleteUserAreaIdSetsToNull() throws Exception {
        reset();
        areaService.delete(areaId);
        assertDelete(1);

        assertNull(profileService.get(VOTER1_ID).getAreaId());
        assertNull(areaService.getRepository().getArea(areaId));
        assertNull(placeService.getRepository().get(testPlaces.getPlace2Id()));
        assertNull(pollService.getRepository().get(testPolls.getActivePoll().getId()));
    }

    @Test
    public void testSearch() throws Exception {
        reset();
        List<EatingArea> chow = areaService.filterByNameStarts("AREA1");
        assertSelect(1);

        assertTrue(chow.size() == 1);
    }
}