package ua.belozorov.lunchvoting.service.area;

import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.lunchplace.JoinAreaRequest;

import java.util.List;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 15.02.17.
 */
public interface JoinAreaRequestService {

    JoinAreaRequest makeJoinRequest(User user, String areaId);

    JoinAreaRequest getJoinRequest(String areaId, String requestId);

    JoinAreaRequest getJoinRequestByRequester(User user, String requestId);

    List<JoinAreaRequest> getJoinRequestsByStatus(String areaId, JoinAreaRequest.JoinStatus status);

    List<JoinAreaRequest> getJoinRequestsByRequester(User user);

    void approveJoinRequest(User approver, String requestId);

    void cancelJoinRequest(User requester, String requestId);

    void rejectJoinRequest(User approver, String requestId);
}
