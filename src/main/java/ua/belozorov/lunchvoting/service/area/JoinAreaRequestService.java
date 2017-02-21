package ua.belozorov.lunchvoting.service.area;

import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.lunchplace.JoinAreaRequest;
import ua.belozorov.lunchvoting.web.security.IsAdmin;
import ua.belozorov.lunchvoting.web.security.IsAdminOrVoter;

import java.util.List;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 15.02.17.
 */
public interface JoinAreaRequestService {

    @IsAdminOrVoter
    JoinAreaRequest make(User user, String areaId);

    @IsAdminOrVoter
    List<JoinAreaRequest> getByRequester(User user);

    @IsAdminOrVoter
    JoinAreaRequest getByRequester(User user, String requestId);

    @IsAdmin
    List<JoinAreaRequest> getByStatus(String areaId, JoinAreaRequest.JoinStatus status);

    @IsAdmin
    void approve(User approver, String requestId);

    @IsAdminOrVoter
    void cancel(User requester, String requestId);

    @IsAdmin
    void reject(User approver, String requestId);
}
