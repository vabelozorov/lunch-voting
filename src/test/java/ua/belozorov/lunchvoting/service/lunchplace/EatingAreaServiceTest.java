package ua.belozorov.lunchvoting.service.lunchplace;

import org.hibernate.LazyInitializationException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import ua.belozorov.lunchvoting.MatcherUtils;
import ua.belozorov.lunchvoting.exceptions.DuplicateDataException;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.UserRole;
import ua.belozorov.lunchvoting.model.lunchplace.AreaTestData;
import ua.belozorov.lunchvoting.model.lunchplace.EatingArea;
import ua.belozorov.lunchvoting.model.lunchplace.JoinAreaRequest;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;
import ua.belozorov.lunchvoting.repository.lunchplace.EatingAreaRepository;
import ua.belozorov.lunchvoting.repository.lunchplace.EatingAreaRepositoryImpl;
import ua.belozorov.lunchvoting.service.AbstractServiceTest;
import ua.belozorov.lunchvoting.service.user.UserService;
import ua.belozorov.lunchvoting.to.AreaTo;
import ua.belozorov.lunchvoting.util.ExceptionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.vladmihalcea.sql.SQLStatementCountValidator.reset;
import static org.junit.Assert.*;
import static ua.belozorov.lunchvoting.MatcherUtils.*;
import static ua.belozorov.lunchvoting.model.UserTestData.*;
import static ua.belozorov.lunchvoting.model.lunchplace.AreaTestData.AREA_COMPARATOR;
import static ua.belozorov.lunchvoting.model.lunchplace.AreaTestData.AREA_TO_COMPARATOR;
import static ua.belozorov.lunchvoting.model.lunchplace.AreaTestData.REQUEST_COMPARATOR;
import static org.hamcrest.Matchers.contains;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 09.02.17.
 */
public class EatingAreaServiceTest extends AbstractServiceTest {

    @Autowired
    private EatingAreaService areaService;

    @Autowired
    private UserService userService;

    @Autowired
    private EatingAreaRepository repository;

    @Test
    public void createArea() throws Exception {
        reset();
        EatingArea expected = areaService.create("ChowChow", ALIEN_USER1);
        assertSql(1, 1,2, 0);

        EatingArea actual = areaService.getArea(expected.getId());
        User alienUser = userService.get(ALIEN_USER1.getId());

        assertThat(actual, matchSingle(expected, AREA_COMPARATOR));
        assertTrue(alienUser.getRoles().contains(UserRole.ADMIN));
    }

    @Test(expected = DuplicateDataException.class)
    public void areaNameIsUnique() throws Exception {
        ExceptionUtils.unwrapException(
                () -> areaService.create("AREA_NAME", ALIEN_USER1),
                ConstraintViolationException.class,
                new DuplicateDataException("")
        );
    }

    @Test
    public void updateAreaName() throws Exception {
        reset();
        areaService.update("NEW_AWESOME_NAME", GOD);
        assertSql(2, 0, 1, 0); //TODO Expected 1 selects

        EatingArea actual = areaService.getArea(GOD.getAreaId());

        assertThat(
                actual,
                matchSingle(testAreas.getFirstArea().changeName("NEW_AWESOME_NAME"), AREA_COMPARATOR)
        );
    }

    @Test
    public void getAreaBasicFieldsOnly() throws Exception {
        EatingArea expected = areaService.create("ChowChow", ALIEN_USER1);

        reset();
        EatingArea actual = areaService.getArea(expected.getId());
        assertSelect(1);

        assertThat(actual, matchSingle(expected, AREA_COMPARATOR));
        assertExceptionCount(LazyInitializationException.class, 3,
                () -> actual.getUsers().size(),
                () -> actual.getPolls().size(),
                () -> actual.getPlaces().size()
        );
    }

    @Test
    public void getAreaWithUsers() throws Exception {
        EatingArea expected = areaService.create("ChowChow", ALIEN_USER1);

        reset();
        TransactionStatus transactionStatus = ptm.getTransaction(new DefaultTransactionDefinition());
        EatingArea actual = repository.getArea(expected.getId(), EatingAreaRepositoryImpl.AreaFields.USERS);
        ptm.commit(transactionStatus);
        assertSelect(2);

        assertThat(expected, matchSingle(actual, AREA_COMPARATOR));
        assertTrue(actual.getUsers().size() == 1);
        assertExceptionCount(LazyInitializationException.class, 2,
                () -> actual.getPolls().size(),
                () -> actual.getPlaces().size()
        );
    }

    @Test
    public void getAreaWithUsersAndPolls() throws Exception {
        EatingArea expected = areaService.create("ChowChow", ALIEN_USER1);

        reset();
        TransactionStatus transactionStatus = ptm.getTransaction(new DefaultTransactionDefinition());
        EatingArea actual = repository.getArea(expected.getId(), EatingAreaRepositoryImpl.AreaFields.USERS, EatingAreaRepositoryImpl.AreaFields.POLLS);
        ptm.commit(transactionStatus);
        assertSelect(3);

        assertThat(expected, matchSingle(actual, AREA_COMPARATOR));
        assertTrue(actual.getUsers().size() == 1);
        assertTrue(actual.getPolls().size() == 0);
        assertExceptionCount(LazyInitializationException.class, 1,
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
    //TODO check that polls and places are deleted too
    public void onAreaDeleteUserAreaIdSetsToNull() throws Exception {
        EatingArea area = areaService.create("ChowChow", ALIEN_USER1);

        //asserting state before deletion
        assertNotNull(areaService.getArea(area.getId()));
        assertEquals(userService.get(ALIEN_USER1.getId()).getAreaId(), area.getId());
        reset();
        areaService.delete(area.getId());

        //asserting state after deletion
        assertDelete(1);
        assertEquals(userService.get(ALIEN_USER1.getId()).getAreaId(), null);
        thrown.expect(NotFoundException.class);
        areaService.getArea(area.getId());
    }

    @Test
    public void testSearch() throws Exception {
        areaService.create("ChowArea", ALIEN_USER1);
        areaService.create("ChowAreaNew", ALIEN_USER1);

        reset();
        List<EatingArea> chow = areaService.filterByNameStarts("Chow");
        assertSelect(1);

        assertTrue(chow.size() == 2);
    }

    @Test
    public void makeJoinRequest() throws Exception {
        reset();
        String areaId = testAreas.getFirstArea().getId();
        JoinAreaRequest expected = areaService.makeJoinRequest(ALIEN_USER1, areaId);
        assertSql(2, 1, 0, 0);
        JoinAreaRequest actual = areaService.getJoinRequest(areaId, expected.getId());

        assertThat(actual, matchSingle(expected, REQUEST_COMPARATOR));
    }

    @Test
    public void getJoinRequest() throws Exception {
        String areaId = testAreas.getFirstArea().getId();
        JoinAreaRequest expected = areaService.makeJoinRequest(ALIEN_USER1, areaId);

        reset();
        JoinAreaRequest actual = areaService.getJoinRequest(areaId, expected.getId());
        assertSelect(1);

        assertThat(actual, matchSingle(expected, REQUEST_COMPARATOR));
    }

    @Test
    public void userCanChangeArea() throws Exception {
        EatingArea chowArea = areaService.create("ChowArea", ALIEN_USER1);
        JoinAreaRequest request = areaService.makeJoinRequest(VOTER, chowArea.getId());
        assertEquals(VOTER.getAreaId(), testAreas.getFirstArea().getId());
        areaService.approveJoinRequest(userService.get(ALIEN_USER1.getId()), request.getId());
        assertEquals(chowArea.getId(), userService.get(VOTER_ID).getAreaId());
    }

    @Test
    public void getJoinRequestByStatus() throws Exception {
        String areaId = areaService.create("ChowArea", ALIEN_USER1).getId();
        JoinAreaRequest request1 = areaService.makeJoinRequest(VOTER1, areaId);
        JoinAreaRequest request2 = areaService.makeJoinRequest(VOTER2, areaId);
        JoinAreaRequest request3 = areaService.makeJoinRequest(VOTER3, areaId);
        areaService.approveJoinRequest(userService.get(ALIEN_USER1.getId()), request1.getId());

        reset();
        List<JoinAreaRequest> requests = areaService.getJoinRequestsByStatus(areaId, JoinAreaRequest.JoinStatus.PENDING);
        assertSelect(1);

        assertThat(
                requests,
                contains(matchCollection(Arrays.asList(request3, request2), REQUEST_COMPARATOR))
        );
    }

    @Test
    public void getJoinRequestsByRequester() throws Exception {
        String areaId = areaService.create("ChowArea", ALIEN_USER1).getId();
        JoinAreaRequest request1 = areaService.makeJoinRequest(VOTER1, areaId);
        JoinAreaRequest request2 = areaService.makeJoinRequest(ALIEN_USER2, testAreas.getFirstArea().getId());
        JoinAreaRequest request3 = areaService.makeJoinRequest(ALIEN_USER2, areaId);

        reset();
        List<JoinAreaRequest> requests = areaService.getJoinRequestsByRequester(ALIEN_USER2);
        assertSelect(1);

        assertThat(
                requests,
                contains(matchCollection(Arrays.asList(request3, request2), REQUEST_COMPARATOR))
        );
    }

    @Test
    public void getSingleJoinRequestByRequester() throws Exception {
        String areaId = areaService.create("ChowArea", ALIEN_USER1).getId();
        JoinAreaRequest request1 = areaService.makeJoinRequest(VOTER1, areaId);
        JoinAreaRequest expected = areaService.makeJoinRequest(ALIEN_USER2, testAreas.getFirstArea().getId());
        JoinAreaRequest request3 = areaService.makeJoinRequest(ALIEN_USER2, areaId);

        reset();
        JoinAreaRequest actual = areaService.getJoinRequestByRequester(ALIEN_USER2, expected.getId());
        assertSelect(1);

        assertThat(actual, matchSingle(expected, REQUEST_COMPARATOR));
    }

    @Test //TODO If requester's area is being changed, ensure that he's not the last admin
    public void approveJoinRequest() throws Exception {
        String areaId = testAreas.getFirstArea().getId();
        JoinAreaRequest request = areaService.makeJoinRequest(ALIEN_USER1, areaId);

        reset();
        areaService.approveJoinRequest(GOD, request.getId());
        assertSql(2, 0, 3, 0);
    }

    @Test(expected = NotFoundException.class)
    public void approverCannotApproveAnotherAreaRequest() throws Exception {
        String areaId = areaService.create("ChowArea", ALIEN_USER1).getId();
        JoinAreaRequest request = areaService.makeJoinRequest(ALIEN_USER2, areaId);

        areaService.approveJoinRequest(GOD, request.getId());
    }

    @Test
    public void requesterCanCancelOwnRequest() throws Exception {
        String areaId = testAreas.getFirstArea().getId();
        JoinAreaRequest request = areaService.makeJoinRequest(ALIEN_USER1, areaId);

        reset();
        areaService.cancelJoinRequest(ALIEN_USER1, request.getId());
        assertSql(1, 0, 1, 0);

        JoinAreaRequest actual = areaService.getJoinRequest(areaId, request.getId());
        assertEquals(actual.getStatus(), JoinAreaRequest.JoinStatus.CANCELLED);
    }

    @Test(expected = NotFoundException.class)
    public void requesterCannotCancelNotOwnRequest() throws Exception {
        String areaId = testAreas.getFirstArea().getId();
        JoinAreaRequest request = areaService.makeJoinRequest(ALIEN_USER1, areaId);
        areaService.cancelJoinRequest(ALIEN_USER2, request.getId());
    }

    @Test
    public void areaAdminRejectsRequest() throws Exception {
        String areaId = testAreas.getFirstArea().getId();
        JoinAreaRequest request = areaService.makeJoinRequest(ALIEN_USER1, areaId);

        reset();
        areaService.rejectJoinRequest(GOD, request.getId());
        assertSql(1, 0, 1, 0);

        JoinAreaRequest actual = areaService.getJoinRequest(areaId, request.getId());
        assertEquals(actual.getStatus(), JoinAreaRequest.JoinStatus.REJECTED);
    }


    @Test(expected = NotFoundException.class)
    public void adminCannotRejectsAnotherAreaRequest() throws Exception {
        String areaId = areaService.create("ChowArea", ALIEN_USER1).getId();
        JoinAreaRequest request = areaService.makeJoinRequest(ALIEN_USER2, areaId);

        reset();
        areaService.rejectJoinRequest(GOD, request.getId());
    }
}