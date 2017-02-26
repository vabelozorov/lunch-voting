package ua.belozorov.lunchvoting.service.area;

import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.lunchplace.JoinAreaRequest;
import ua.belozorov.lunchvoting.web.security.IsAdmin;
import ua.belozorov.lunchvoting.web.security.IsAdminOrVoter;

import java.util.List;

/**

 *
 * Created on 15.02.17.
 */
public interface JoinAreaRequestService {

    JoinAreaRequest make(User user, String areaId);

    List<JoinAreaRequest> getByRequester(User user);

    JoinAreaRequest getByRequester(User user, String requestId);

    List<JoinAreaRequest> getByStatus(String areaId, JoinAreaRequest.JoinStatus status);

    void approve(User approver, String requestId);

    void cancel(User requester, String requestId);

    void reject(User approver, String requestId);
}
