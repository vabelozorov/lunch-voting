package ua.belozorov.lunchvoting.service.area;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
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

import static com.vladmihalcea.sql.SQLStatementCountValidator.reset;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
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
    private EatingAreaService areaService;

    @Autowired
    private JoinAreaRequestService requestService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserProfileService profileService;

    @Autowired
    private EatingAreaRepository repository;

    @Test
    public void makeJoinRequest() throws Exception {
        reset();
        String areaId = testAreas.getFirstArea().getId();
        JoinAreaRequest expected = requestService.makeJoinRequest(ALIEN_USER1, areaId);
        assertSql(2, 1, 0, 0);
        JoinAreaRequest actual = requestService.getJoinRequest(areaId, expected.getId());

        assertThat(actual, matchSingle(expected, REQUEST_COMPARATOR));
    }

    @Test
    public void getJoinRequest() throws Exception {
        String areaId = testAreas.getFirstArea().getId();
        JoinAreaRequest expected = requestService.makeJoinRequest(ALIEN_USER1, areaId);

        reset();
        JoinAreaRequest actual = requestService.getJoinRequest(areaId, expected.getId());
        assertSelect(1);

        assertThat(actual, matchSingle(expected, REQUEST_COMPARATOR));
    }

    @Test
    public void userCanChangeArea() throws Exception {
        EatingArea chowArea = areaService.create("ChowArea", ALIEN_USER1);
        String areaId = chowArea.getId();

        JoinAreaRequest request = requestService.makeJoinRequest(VOTER, areaId);

        assertEquals(VOTER.getAreaId(), testAreas.getFirstArea().getId());

        requestService.approveJoinRequest(userService.get(areaId, ALIEN_USER1.getId()), request.getId());

        assertEquals(areaId, userService.get(areaId, VOTER_ID).getAreaId());
    }

    @Test
    public void getJoinRequestByStatus() throws Exception {
        String areaId = areaService.create("ChowArea", ALIEN_USER1).getId();
        JoinAreaRequest request1 = requestService.makeJoinRequest(VOTER1, areaId);
        JoinAreaRequest request2 = requestService.makeJoinRequest(VOTER2, areaId);
        JoinAreaRequest request3 = requestService.makeJoinRequest(VOTER3, areaId);
        requestService.approveJoinRequest(userService.get(areaId, ALIEN_USER1.getId()), request1.getId());

        reset();
        List<JoinAreaRequest> requests = requestService.getJoinRequestsByStatus(areaId, JoinAreaRequest.JoinStatus.PENDING);
        assertSelect(1);

        assertThat(
                requests,
                contains(matchCollection(Arrays.asList(request3, request2), REQUEST_COMPARATOR))
        );
    }

    @Test
    public void getJoinRequestsByRequester() throws Exception {
        String areaId = areaService.create("ChowArea", ALIEN_USER1).getId();
        JoinAreaRequest request1 = requestService.makeJoinRequest(VOTER1, areaId);
        JoinAreaRequest request2 = requestService.makeJoinRequest(ALIEN_USER2, testAreas.getFirstArea().getId());
        JoinAreaRequest request3 = requestService.makeJoinRequest(ALIEN_USER2, areaId);

        reset();
        List<JoinAreaRequest> requests = requestService.getJoinRequestsByRequester(ALIEN_USER2);
        assertSelect(1);

        assertThat(
                requests,
                contains(matchCollection(Arrays.asList(request3, request2), REQUEST_COMPARATOR))
        );
    }

    @Test
    public void getSingleJoinRequestByRequester() throws Exception {
        String areaId = areaService.create("ChowArea", ALIEN_USER1).getId();
        JoinAreaRequest request1 = requestService.makeJoinRequest(VOTER1, areaId);
        JoinAreaRequest expected = requestService.makeJoinRequest(ALIEN_USER2, testAreas.getFirstArea().getId());
        JoinAreaRequest request3 = requestService.makeJoinRequest(ALIEN_USER2, areaId);

        reset();
        JoinAreaRequest actual = requestService.getJoinRequestByRequester(ALIEN_USER2, expected.getId());
        assertSelect(1);

        assertThat(actual, matchSingle(expected, REQUEST_COMPARATOR));
    }

    @Test
    public void approveJoinRequest() throws Exception {
        String areaId = testAreas.getFirstArea().getId();
        JoinAreaRequest request = requestService.makeJoinRequest(ALIEN_USER1, areaId);

        reset();
        requestService.approveJoinRequest(GOD, request.getId());
        assertSql(3, 0, 3, 0);

        JoinAreaRequest approvedReq = requestService.getJoinRequest(areaId, request.getId());
        User requester = approvedReq.getRequester();

        assertEquals(JoinAreaRequest.JoinStatus.APPROVED, approvedReq.getStatus());
        assertEquals(areaId, requester.getAreaId());

        TransactionStatus transactionStatus = ptm.getTransaction(new DefaultTransactionDefinition());
        assertTrue(repository.getArea(areaId, EatingAreaRepositoryImpl.Fields.USERS).getUsers()
                .contains(requester)
        );
        ptm.commit(transactionStatus);
    }

    @Test(expected = NotFoundException.class)
    public void approverCannotApproveAnotherAreaRequest() throws Exception {
        String areaId = areaService.create("ChowArea", ALIEN_USER1).getId();
        JoinAreaRequest request = requestService.makeJoinRequest(ALIEN_USER2, areaId);

        requestService.approveJoinRequest(GOD, request.getId());
    }

    @Test
    public void requesterCanCancelOwnRequest() throws Exception {
        String areaId = testAreas.getFirstArea().getId();
        JoinAreaRequest request = requestService.makeJoinRequest(ALIEN_USER1, areaId);

        reset();
        requestService.cancelJoinRequest(ALIEN_USER1, request.getId());
        assertSql(1, 0, 1, 0);

        JoinAreaRequest actual = requestService.getJoinRequest(areaId, request.getId());
        assertEquals(actual.getStatus(), JoinAreaRequest.JoinStatus.CANCELLED);
    }

    @Test(expected = NotFoundException.class)
    public void requesterCannotCancelNotOwnRequest() throws Exception {
        String areaId = testAreas.getFirstArea().getId();
        JoinAreaRequest request = requestService.makeJoinRequest(ALIEN_USER1, areaId);
        requestService.cancelJoinRequest(ALIEN_USER2, request.getId());
    }

    @Test
    public void areaAdminRejectsRequest() throws Exception {
        String areaId = testAreas.getFirstArea().getId();
        JoinAreaRequest request = requestService.makeJoinRequest(ALIEN_USER1, areaId);

        reset();
        requestService.rejectJoinRequest(GOD, request.getId());
        assertSql(1, 0, 1, 0);

        JoinAreaRequest actual = requestService.getJoinRequest(areaId, request.getId());
        assertEquals(actual.getStatus(), JoinAreaRequest.JoinStatus.REJECTED);
    }


    @Test(expected = NotFoundException.class)
    public void adminCannotRejectsAnotherAreaRequest() throws Exception {
        String areaId = areaService.create("ChowArea", ALIEN_USER1).getId();
        JoinAreaRequest request = requestService.makeJoinRequest(ALIEN_USER2, areaId);

        reset();
        requestService.rejectJoinRequest(GOD, request.getId());
    }
}
