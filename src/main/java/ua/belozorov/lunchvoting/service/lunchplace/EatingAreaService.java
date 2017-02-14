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

    /**
     * Saves a new User instance and assigns it to Eating area signified by {@code areaId} parameter.
     * User's ID, email, password and name are provided by {@code user} parameter
     * and other values are set to default values
     * @param user non-null user instance
     * @param areaId non-null ID of existing EatingArea entity
     * @return persisted User instance
     */
    User createUserInArea(String areaId, User user);

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

    /**
     * Adds already persisted User {@code user} to an EatingArea {@code area}
     * EatingArea#users collection must be initialized
     * @param areaId persisted area
     * @param member persisted User entity
     */
    void addMember(String areaId, User member);
}
