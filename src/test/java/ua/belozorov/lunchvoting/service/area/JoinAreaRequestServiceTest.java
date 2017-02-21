package ua.belozorov.lunchvoting.service.area;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import ua.belozorov.lunchvoting.WithMockAdmin;
import ua.belozorov.lunchvoting.WithMockVoter;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.lunchplace.EatingArea;
import ua.belozorov.lunchvoting.model.lunchplace.JoinAreaRequest;
import ua.belozorov.lunchvoting.repository.lunchplace.EatingAreaRepository;
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
 * <h2></h2>
 *
 * @author vabelozorov on 15.02.17.
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

    @Test
    @WithMockVoter
    public void makeJoinRequest() throws Exception {
        String areaId = testAreas.getFirstArea().getId();

        reset();
        JoinAreaRequest expected = requestService.make(ALIEN_USER1, areaId);
        assertSql(2, 1, 0, 0);

        JoinAreaRequest actual = areaService.getRepository()
                .getJoinRequest(areaId, expected.getId());

        assertThat(actual, matchSingle(expected, REQUEST_COMPARATOR));
    }

    @Test
    @WithMockVoter
    public void userCanChangeArea() throws Exception {
        String areaId = testAreas.getFirstAreaId();
        JoinAreaRequest request = requestService.make(A2_USER1, areaId);

        asAdmin(() -> {
            requestService.approve(GOD, request.getId());
            return null;
        });

        User updatedUser = profileService.getRepository().get(areaId, A2_USER1.getId());
        assertEquals(areaId, updatedUser.getAreaId());
    }

    @Test
    @WithMockAdmin
    public void getJoinRequestByStatus() throws Exception {
        String areaId = asVoter(() -> areaService.create("ChowArea", ALIEN_USER1).getId());
        JoinAreaRequest request1 = asVoter(() -> requestService.make(VOTER1, areaId));
        JoinAreaRequest request2 = asVoter(() -> requestService.make(VOTER2, areaId));
        JoinAreaRequest request3 = asVoter(() -> requestService.make(VOTER3, areaId));
        requestService.approve(
                ALIEN_USER1.assignAreaId(areaId),
                request1.getId()
        );

        reset();
        List<JoinAreaRequest> requests = requestService.getByStatus(areaId, JoinAreaRequest.JoinStatus.PENDING);
        assertSelect(1);

        assertThat(
                requests,
                contains(matchCollection(Arrays.asList(request3, request2), REQUEST_COMPARATOR))
        );
    }

    @Test
    @WithMockVoter
    public void getJoinRequestsByRequester() throws Exception {
        String areaId = areaService.create("ChowArea", ALIEN_USER1).getId();
        JoinAreaRequest request1 = requestService.make(VOTER1, areaId);
        JoinAreaRequest request2 = requestService.make(ALIEN_USER2, testAreas.getFirstArea().getId());
        JoinAreaRequest request3 = requestService.make(ALIEN_USER2, areaId);

        reset();
        List<JoinAreaRequest> requests = requestService.getByRequester(ALIEN_USER2);
        assertSelect(1);

        assertThat(
                requests,
                contains(matchCollection(Arrays.asList(request3, request2), REQUEST_COMPARATOR))
        );
    }

    @Test
    @WithMockVoter
    public void getSingleJoinRequestByRequester() throws Exception {
        String areaId = areaService.create("ChowArea", ALIEN_USER1).getId();
        JoinAreaRequest request1 = requestService.make(VOTER1, areaId);
        JoinAreaRequest expected = requestService.make(ALIEN_USER2, testAreas.getFirstArea().getId());
        JoinAreaRequest request3 = requestService.make(ALIEN_USER2, areaId);

        reset();
        JoinAreaRequest actual = requestService.getByRequester(ALIEN_USER2, expected.getId());
        assertSelect(1);

        assertThat(actual, matchSingle(expected, REQUEST_COMPARATOR));
    }

    @Test
    @WithMockAdmin
    public void onRequestApprovalItsStatusUpdatedAndTimeSet() throws Exception {
        String areaId = testAreas.getFirstArea().getId();
        JoinAreaRequest request = asVoter(() -> requestService.make(ALIEN_USER1, areaId));

        reset();
        requestService.approve(GOD, request.getId());
        assertSql(3, 0, 3, 0);

        JoinAreaRequest approvedReq = areaService.getRepository()
                                        .getJoinRequest(areaId, request.getId());

        assertEquals(JoinAreaRequest.JoinStatus.APPROVED, approvedReq.getStatus());
        assertNotNull(approvedReq.getDecidedOn());
    }

    @Test
    @WithMockAdmin
    public void onRequestApprovalAreaIdOfUserIsSet() throws Exception {
        String areaId = testAreas.getFirstArea().getId();
        JoinAreaRequest request = asVoter(() -> requestService.make(ALIEN_USER1, areaId));

        reset();
        requestService.approve(GOD, request.getId());
        assertSql(3, 0, 3, 0);

        JoinAreaRequest approvedReq = areaService.getRepository()
                .getJoinRequest(areaId, request.getId());
        User requester = approvedReq.getRequester();

        assertEquals(areaId, requester.getAreaId());
    }

    @Test
    @WithMockAdmin
    public void onRequestApprovalUserIsInAreaUsersCollection() throws Exception {
        String areaId = testAreas.getFirstArea().getId();
        JoinAreaRequest request = asVoter(() -> requestService.make(ALIEN_USER1, areaId));

        reset();
        requestService.approve(GOD, request.getId());
        assertSql(3, 0, 3, 0);

        TransactionStatus transactionStatus = ptm.getTransaction(new DefaultTransactionDefinition());
        Set<User> users = areaService.getRepository()
                .getArea(areaId, EatingAreaRepositoryImpl.Fields.USERS).getUsers();
        assertTrue(users.contains(ALIEN_USER1));
        ptm.commit(transactionStatus);
    }

    @Test(expected = NotFoundException.class)
    @WithMockAdmin
    public void approverCannotApproveAnotherAreaRequest() throws Exception {
        String areaId = asVoter(() -> areaService.create("ChowArea", ALIEN_USER1).getId());
        JoinAreaRequest request = asVoter(() -> requestService.make(ALIEN_USER2, areaId));

        requestService.approve(GOD, request.getId());
    }

    @Test
    @WithMockVoter
    public void requesterCanCancelOwnRequest() throws Exception {
        String areaId = testAreas.getFirstArea().getId();
        JoinAreaRequest request = requestService.make(ALIEN_USER1, areaId);

        reset();
        requestService.cancel(ALIEN_USER1, request.getId());
        assertSql(1, 0, 1, 0);

        JoinAreaRequest actual = areaService.getRepository()
                                    .getJoinRequest(areaId, request.getId());
        assertEquals(actual.getStatus(), JoinAreaRequest.JoinStatus.CANCELLED);
    }

    @Test(expected = NotFoundException.class)
    @WithMockVoter
    public void requesterCannotCancelNotOwnRequest() throws Exception {
        String areaId = testAreas.getFirstArea().getId();
        JoinAreaRequest request = requestService.make(ALIEN_USER1, areaId);
        requestService.cancel(ALIEN_USER2, request.getId());
    }

    @Test
    @WithMockAdmin
    public void areaAdminRejectsRequest() throws Exception {
        String areaId = testAreas.getFirstArea().getId();
        JoinAreaRequest request = asVoter(() -> requestService.make(ALIEN_USER1, areaId));

        reset();
        requestService.reject(GOD, request.getId());
        assertSql(1, 0, 1, 0);

        JoinAreaRequest actual = areaService.getRepository()
                .getJoinRequest(areaId, request.getId());
        assertEquals(actual.getStatus(), JoinAreaRequest.JoinStatus.REJECTED);
    }


    @Test(expected = NotFoundException.class)
    @WithMockAdmin
    public void adminCannotRejectsAnotherAreaRequest() throws Exception {
        String areaId = testAreas.getFirstAreaId();
        JoinAreaRequest request = asVoter(() -> requestService.make(A2_USER1, areaId));

        requestService.reject(A2_ADMIN, request.getId());
    }
}
