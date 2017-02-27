package ua.belozorov.lunchvoting.service.area;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.lunchplace.JoinAreaRequest;
import ua.belozorov.lunchvoting.repository.lunchplace.EatingAreaRepositoryImpl;
import ua.belozorov.lunchvoting.service.AbstractServiceTest;
import ua.belozorov.lunchvoting.service.user.UserProfileService;
import ua.belozorov.lunchvoting.service.user.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.vladmihalcea.sql.SQLStatementCountValidator.reset;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;
import static ua.belozorov.lunchvoting.MatcherUtils.*;
import static ua.belozorov.lunchvoting.MatcherUtils.assertSql;
import static ua.belozorov.lunchvoting.model.UserTestData.*;
import static ua.belozorov.lunchvoting.model.lunchplace.AreaTestData.REQUEST_COMPARATOR;

/**
 * This test suite in the beginning of each test query a DB for user(s), while they are available from UserTestData.
 * This is due to the fact, that execution of {@link JoinAreaRequestService#make(User, String)} re-attaches
 * a User object to a persistent context, resulting in revealing a mismatch between
 * @Version attribute of that UserTestData object and a corresponding persisted object.
 *
 * Created on 15.02.17.
 */
public class JoinAreaRequestServiceTest extends AbstractServiceTest {

    @Autowired
    private JoinAreaRequestService requestService;

    @Autowired
    private EatingAreaService areaService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserProfileService profileService;

    private final String areaId = testAreas.getFirstAreaId();

    @Test
    public void makeJoinRequest() throws Exception {
        User requester = profileService.get(ALIEN_USER1.getId());

        reset();
        JoinAreaRequest expected = requestService.make(requester, areaId);
        assertSql(2, 1, 0, 0);

        JoinAreaRequest actual = areaService.getRepository()
                .getJoinRequest(areaId, expected.getId());

        assertThat(actual, matchSingle(expected, REQUEST_COMPARATOR));
    }

    @Test
    public void userCanChangeArea() throws Exception {
        User requester = profileService.get(A2_USER1.getId());
        JoinAreaRequest request = requestService.make(requester, areaId);
        requestService.approve(GOD, request.getId());
        User updatedUser = profileService.getRepository().get(areaId, A2_USER1.getId());
        assertEquals(areaId, updatedUser.getAreaId());
    }

    @Test
    public void getJoinRequestByStatus() throws Exception {
        User requester1 = profileService.get(VOTER1_ID);
        User requester2 = profileService.get(VOTER2_ID);
        User requester3 = profileService.get(VOTER3_ID);

        JoinAreaRequest request1 = requestService.make(requester1, testAreas.getSecondAreaId());
        JoinAreaRequest request2 = requestService.make(requester2, testAreas.getSecondAreaId());
        JoinAreaRequest request3 = requestService.make(requester3, testAreas.getSecondAreaId());
        requestService.approve(A2_ADMIN, request1.getId());

        reset();
        List<JoinAreaRequest> requests = requestService.getByStatus(testAreas.getSecondAreaId(), JoinAreaRequest.JoinStatus.PENDING);
        assertSelect(1);

        assertThat(
                requests,
                contains(matchCollection(Arrays.asList(request3, request2), REQUEST_COMPARATOR))
        );
    }

    @Test
    public void getJoinRequestsByRequester() throws Exception {
        User requester1 = profileService.get(VOTER1_ID);
        User requester2 = profileService.get(ALIEN_USER2.getId());

        JoinAreaRequest request1 = requestService.make(requester1, testAreas.getSecondAreaId());
        JoinAreaRequest request2 = requestService.make(requester2, testAreas.getFirstAreaId());
        JoinAreaRequest request3 = requestService.make(requester2, testAreas.getSecondAreaId());

        reset();
        List<JoinAreaRequest> requests = requestService.getByRequester(requester2);
        assertSelect(1);

        assertThat(
                requests,
                contains(matchCollection(Arrays.asList(request3, request2), REQUEST_COMPARATOR))
        );
    }

    @Test
    public void getSingleJoinRequestByRequester() throws Exception {
        User requester1 = profileService.get(VOTER1_ID);
        User requester2 = profileService.get(ALIEN_USER2.getId());

        JoinAreaRequest request1 = requestService.make(requester1, testAreas.getSecondAreaId());
        JoinAreaRequest expected = requestService.make(requester2, testAreas.getFirstAreaId());
        JoinAreaRequest request3 = requestService.make(requester2, testAreas.getSecondAreaId());

        reset();
        JoinAreaRequest actual = requestService.getByRequester(requester2, expected.getId());
        assertSelect(1);

        assertThat(actual, matchSingle(expected, REQUEST_COMPARATOR));
    }

    @Test
    public void onRequestApprovalItsStatusUpdatedAndTimeSet() throws Exception {
        User requester = profileService.get(ALIEN_USER2.getId());
        JoinAreaRequest request = requestService.make(requester, areaId);

        reset();
        requestService.approve(GOD, request.getId());
        assertSql(3, 0, 3, 0);

        JoinAreaRequest approvedReq = areaService.getRepository()
                                        .getJoinRequest(areaId, request.getId());

        assertEquals(JoinAreaRequest.JoinStatus.APPROVED, approvedReq.getStatus());
        assertNotNull(approvedReq.getDecidedOn());
    }

    @Test
    public void onRequestApprovalAreaIdOfUserIsSet() throws Exception {
        User requester = profileService.get(ALIEN_USER2.getId());
        JoinAreaRequest request = requestService.make(requester, areaId);

        reset();
        requestService.approve(GOD, request.getId());
        assertSql(3, 0, 3, 0);

        JoinAreaRequest approvedReq = areaService.getRepository()
                .getJoinRequest(areaId, request.getId());

        assertEquals(areaId, approvedReq.getRequester().getAreaId());
    }

    @Test
    public void onRequestApprovalUserIsInAreaUsersCollection() throws Exception {
        User requester = profileService.get(ALIEN_USER2.getId());
        JoinAreaRequest request = requestService.make(requester, areaId);

        reset();
        requestService.approve(GOD, request.getId());
        assertSql(3, 0, 3, 0);

        TransactionStatus transactionStatus = ptm.getTransaction(new DefaultTransactionDefinition());
        Set<User> users = areaService.getRepository()
                .getArea(areaId, EatingAreaRepositoryImpl.Fields.USERS).getVoters();

        assertTrue(users.contains(requester));
        ptm.commit(transactionStatus);
    }

    @Test(expected = NotFoundException.class)
    public void approverCannotApproveAnotherAreaRequest() throws Exception {
        User requester = profileService.get(ALIEN_USER2.getId());
        JoinAreaRequest request = requestService.make(requester, areaId);

        requestService.approve(A2_ADMIN, request.getId());
    }

    @Test
    public void requesterCanCancelOwnRequest() throws Exception {
        User requester = profileService.get(ALIEN_USER2.getId());
        JoinAreaRequest request = requestService.make(requester, areaId);

        reset();
        requestService.cancel(requester, request.getId());
        assertSql(1, 0, 1, 0);

        JoinAreaRequest actual = areaService.getRepository()
                                    .getJoinRequest(areaId, request.getId());
        assertEquals(actual.getStatus(), JoinAreaRequest.JoinStatus.CANCELLED);
    }

    @Test(expected = NotFoundException.class)
    public void requesterCannotCancelNotOwnRequest() throws Exception {
        User requester = profileService.get(ALIEN_USER2.getId());
        JoinAreaRequest request = requestService.make(requester, areaId);
        requestService.cancel(ALIEN_USER1, request.getId());
    }

    @Test
    public void areaAdminRejectsRequest() throws Exception {
        User requester = profileService.get(ALIEN_USER2.getId());
        JoinAreaRequest request = requestService.make(requester, areaId);

        reset();
        requestService.reject(GOD, request.getId());
        assertSql(1, 0, 1, 0);

        JoinAreaRequest actual = areaService.getRepository()
                .getJoinRequest(areaId, request.getId());
        assertEquals(actual.getStatus(), JoinAreaRequest.JoinStatus.REJECTED);
    }


    @Test(expected = NotFoundException.class)
    public void adminCannotRejectsAnotherAreaRequest() throws Exception {
        User requester = profileService.get(ALIEN_USER2.getId());
        JoinAreaRequest request = requestService.make(requester, areaId);

        requestService.reject(A2_ADMIN, request.getId());
    }
}
