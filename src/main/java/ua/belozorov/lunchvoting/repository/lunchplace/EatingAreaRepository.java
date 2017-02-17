package ua.belozorov.lunchvoting.repository.lunchplace;

import org.jetbrains.annotations.Nullable;
import ua.belozorov.lunchvoting.model.lunchplace.EatingArea;
import ua.belozorov.lunchvoting.model.lunchplace.JoinAreaRequest;
import ua.belozorov.lunchvoting.to.AreaTo;

import java.util.List;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 08.02.17.
 */
public interface EatingAreaRepository {

    EatingArea save(EatingArea domain);

    boolean delete(String areaId);

    EatingArea getArea(String areaId, EatingAreaRepositoryImpl.Fields... fields);

    AreaTo getAreaTo(String areaId);

    AreaTo getAreaToSummary(String areaId);

    @Nullable JoinAreaRequest getJoinRequest(String areaId, String requestId);

    List<EatingArea> getByNameStarts(String search);

    JoinAreaRequest save(JoinAreaRequest request);

    JoinAreaRequest update(JoinAreaRequest request);

    EatingArea update(EatingArea area);

    List<JoinAreaRequest> getJoinRequestsByStatus(String areaId, JoinAreaRequest.JoinStatus status);

    List<JoinAreaRequest> getJoinRequestsByRequester(String requesterId);

    JoinAreaRequest getJoinRequestByRequester(String requesterId, String requestId);
}
