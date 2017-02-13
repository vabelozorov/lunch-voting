package ua.belozorov.lunchvoting.service.lunchplace;

import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.lunchplace.EatingArea;
import ua.belozorov.lunchvoting.model.lunchplace.JoinAreaRequest;
import ua.belozorov.lunchvoting.to.AreaTo;

import java.util.List;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 08.02.17.
 */
public interface EatingAreaService {

    EatingArea create(String name, User user);

    void update(String name, User user);

    EatingArea getArea(String areaId);

    AreaTo getAsTo(String areaId, boolean summary);

    List<EatingArea> filterByNameStarts(String search);

    void delete(String areaId);

    JoinAreaRequest makeJoinRequest(User user, String areaId);

    JoinAreaRequest getJoinRequest(String areaId, String requestId);

    JoinAreaRequest getJoinRequestByRequester(User user, String requestId);

    List<JoinAreaRequest> getJoinRequestsByStatus(String areaId, JoinAreaRequest.JoinStatus status);

    List<JoinAreaRequest> getJoinRequestsByRequester(User user);

    void approveJoinRequest(User approver, String requestId);

    void cancelJoinRequest(User requester, String requestId);

    void rejectJoinRequest(User approver, String requestId);
}
