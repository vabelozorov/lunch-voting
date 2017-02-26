package ua.belozorov.lunchvoting.service.area;

import com.google.common.collect.ImmutableSet;
import org.hibernate.LazyInitializationException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import ua.belozorov.lunchvoting.WithMockAdmin;
import ua.belozorov.lunchvoting.WithMockVoter;
import ua.belozorov.lunchvoting.exceptions.DuplicateDataException;
import ua.belozorov.lunchvoting.exceptions.NoAreaAdminException;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.UserRole;
import ua.belozorov.lunchvoting.model.lunchplace.AreaTestData;
import ua.belozorov.lunchvoting.model.lunchplace.EatingArea;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;
import ua.belozorov.lunchvoting.repository.lunchplace.EatingAreaRepository;
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
import static ua.belozorov.lunchvoting.model.voting.polling.PollTestData.*;
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
    @WithMockVoter
    public void createArea() throws Exception {
        reset();
        EatingArea expected = areaService.create("ChowChow", ALIEN_USER1);
        assertSql(3, 1,3, 0);

        EatingArea actual = areaService.getRepository().getArea(expected.getId());
        User alienUser = profileService.getRepository()
                            .get(null, ALIEN_USER1.getId());

        assertThat(actual, matchSingle(expected, AREA_COMPARATOR));
        assertTrue(alienUser.getRoles().contains(UserRole.ADMIN));
    }

    @Test(expected = NoAreaAdminException.class)
    public void theOnlyAdminInAreaCannotCreateNewArea() throws Exception {
        asAdmin(() -> {userService.setRoles(areaId, ADMIN_ID, Collections.singleton(UserRole.VOTER)); return null;});
        asVoter(() -> areaService.create("NEW_AWESOME_SO_MUCH_BETTER_AREA", GOD));
    }

    @Test(expected = DuplicateDataException.class)
    @WithMockVoter
    public void areaNameIsUnique() throws Exception {
        ExceptionUtils.executeAndUnwrapException(
                () -> areaService.create("AREA_NAME", ALIEN_USER1),
                ConstraintViolationException.class,
                new DuplicateDataException(ErrorCode.DUPLICATE_AREA_NAME, new Object[]{"AREA_NAME"})
        );
    }

    @Test
    @WithMockAdmin
    public void updateAreaName() throws Exception {
        reset();
        areaService.updateAreaName("NEW_AWESOME_NAME", GOD);
        assertSql(1, 0, 1, 0);

        EatingArea actual = areaService.getRepository().getArea(GOD.getAreaId());

        assertThat(
                actual,
                matchSingle(testAreas.getFirstArea().changeName("NEW_AWESOME_NAME"), AREA_COMPARATOR)
        );
    }

    @Test
    @WithMockAdmin
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
        EatingArea area = areaService.getRepository()
                .getArea(areaId, EatingAreaRepositoryImpl.Fields.USERS);

        assertTrue(area.getUsers().contains(created));
        ptm.commit(transactionStatus);
    }

    @Test
    @WithMockAdmin
    public void createPlaceInArea() throws Exception {
        Set<String> phones = ImmutableSet.of("0661234567", "0441234567", "0123456789", "1234567890");

        reset();
        LunchPlace created = areaService.createPlaceInArea(testAreas.getFirstAreaId(),
                "NEW_ID", "NEW_PLACE_NAME", "new address", "new description", phones);
        assertSql(2, 1, 2, 0);

        phones = phones.stream().sorted().collect(Collectors.toSet());
        LunchPlace actual = placeService.getRepository().get(areaId, created.getId());
        LunchPlace expected = created.toBuilder().phones(phones).build();

        assertThat(
                actual,
                matchSingle(expected, LUNCH_PLACE_COMPARATOR)
        );
    }

    @Test
    @WithMockAdmin
    public void createPollInAreaForMenuDate() throws Exception {
        reset();
        LunchPlacePoll expected = areaService.createPollInArea(areaId, NOW_DATE.plusDays(2), null, null, null);
        assertSql(3, 3, 4, 0); //TODO expected 2 update; consider changing LunchPlacePoll#pollItems to Set?

        LunchPlacePoll actual = pollService.getRepository().getWithPollItems(areaId, expected.getId());

        assertThat(actual, matchSingle(expected, POLL_COMPARATOR.noVotes()));
    }

    @Test
    @WithMockVoter
    public void getAreaBasicFieldsOnly() throws Exception {
        EatingArea expected = areaService.create("ChowChow", ALIEN_USER1);

        reset();
        EatingArea actual = areaService.getRepository().getArea(expected.getId());
        assertSelect(1);

        assertThat(actual, matchSingle(expected, AREA_COMPARATOR));
        assertExceptionCount(LazyInitializationException.class,
                () -> actual.getUsers().size(),
                () -> actual.getPolls().size(),
                () -> actual.getPlaces().size()
        );
    }

    @Test
    @WithMockVoter
    public void getAreaWithUsers() throws Exception {
        EatingArea expected = testAreas.getFirstArea();

        reset();
        TransactionStatus transactionStatus = ptm.getTransaction(new DefaultTransactionDefinition());
        EatingArea actual = areaService.getRepository()
                .getArea(expected.getId(), EatingAreaRepositoryImpl.Fields.USERS);
        ptm.commit(transactionStatus);
        assertSelect(2);

        assertThat(expected, matchSingle(actual, AREA_COMPARATOR));
        assertTrue(actual.getUsers().size() == 7);
        assertExceptionCount(LazyInitializationException.class,
                () -> actual.getPolls().size(),
                () -> actual.getPlaces().size()
        );
    }

    @Test
    @WithMockVoter
    public void getAreaWithUsersAndPolls() throws Exception {
        EatingArea expected = testAreas.getFirstArea();

        reset();
        TransactionStatus transactionStatus = ptm.getTransaction(new DefaultTransactionDefinition());
        EatingArea actual = areaService.getRepository().getArea(expected.getId(), EatingAreaRepositoryImpl.Fields.USERS, EatingAreaRepositoryImpl.Fields.POLLS);
        ptm.commit(transactionStatus);
        assertSelect(3);

        assertThat(expected, matchSingle(actual, AREA_COMPARATOR));
        assertTrue(actual.getUsers().size() == 7);
        assertTrue(actual.getPolls().size() == 4);
        assertExceptionCount(LazyInitializationException.class,
                () -> actual.getPlaces().size()
        );
    }

    @Test
    @WithMockVoter
    public void getAreaAsToNoSummary() throws Exception {
        AreaTo actual = areaService.getAsTo(testAreas.getFirstAreaId(), false);
        AreaTo expected = AreaTestData.dto(testAreas.getFirstArea());
        assertThat(actual, matchByToString(expected));
    }

    @Test
    @WithMockVoter
    public void getAreaAsToWithSummary() throws Exception {
        AreaTo actual = areaService.getAsTo(testAreas.getFirstAreaId(), true);
        AreaTo expected = AreaTestData.dtoSummary(testAreas.getFirstArea());
        assertThat(actual, matchSingle(expected, AREA_TO_COMPARATOR));
    }

    @Test
    //TODO check that polls and places are deleted too
    @WithMockAdmin
    public void onAreaDeleteUserAreaIdSetsToNull() throws Exception {
        String areaId = testAreas.getFirstAreaId();

        //asserting state before deletion
        String userArea = profileService.getRepository().get(null, VOTER1_ID).getAreaId();
        assertEquals(userArea, areaId);

        reset();
        areaService.delete(areaId);
        assertDelete(1);

        //asserting state after deletion
        assertNull(profileService.getRepository().get(null, VOTER1_ID).getAreaId());
        assertNull(areaService.getRepository().getArea(areaId));
    }

    @Test
    @WithMockVoter
    public void testSearch() throws Exception {
        areaService.create("ChowArea", ALIEN_USER1);
        areaService.create("ChowAreaNew", ALIEN_USER1);

        reset();
        List<EatingArea> chow = areaService.getRepository().getByNameStarts("Chow");
        assertSelect(1);

        assertTrue(chow.size() == 2);
    }
}