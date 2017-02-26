package ua.belozorov.lunchvoting.service.area;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.lunchplace.EatingArea;
import ua.belozorov.lunchvoting.model.lunchplace.JoinAreaRequest;
import ua.belozorov.lunchvoting.repository.lunchplace.EatingAreaRepository;

import java.util.List;

import static java.util.Optional.ofNullable;

/**

 *
 * Created on 15.02.17.
 */
@Service
public final class JoinAreaRequestServiceImpl implements JoinAreaRequestService {

    private final EatingAreaService areaService;
    private final EatingAreaRepository areaRepository;

    @Autowired
    public JoinAreaRequestServiceImpl(EatingAreaService areaService, EatingAreaRepository areaRepository) {
        this.areaService = areaService;
        this.areaRepository = areaRepository;
    }

    @Override
    public List<JoinAreaRequest> getByStatus(String areaId, JoinAreaRequest.JoinStatus status) {
        return areaRepository.getJoinRequestsByStatus(areaId, status);
    }

    @Override
    public List<JoinAreaRequest> getByRequester(User user) {
        return areaRepository.getJoinRequestsByRequester(user.getId());
    }

    @Override
    public JoinAreaRequest getByRequester(User user, String requestId) {
        return ofNullable(areaRepository.getJoinRequestByRequester(user.getId(), requestId))
                .orElseThrow(() -> new NotFoundException(requestId, JoinAreaRequest.class));
    }

    @Override
    @Transactional
    public JoinAreaRequest make(User requester, String areaId) {
        EatingArea area = areaRepository.getArea(areaId);
        JoinAreaRequest request = new JoinAreaRequest(requester, area);
        return areaRepository.save(request);
    }

    @Override
    @Transactional
    public void approve(User approver, String requestId) {
        String areaId = approver.getAreaId();
        JoinAreaRequest request = this.get(areaId, requestId);
        areaRepository.update(request.approve());

        areaService.addMember(areaId, request.getRequester());
    }

    @Override
    @Transactional
    public void cancel(User requester, String requestId) {
        JoinAreaRequest request = this.getByRequester(requester, requestId);
        areaRepository.update(request.cancel());
    }

    @Override
    @Transactional
    public void reject(User approver, String requestId) {
        JoinAreaRequest request = this.get(approver.getAreaId(), requestId);
        areaRepository.update(request.reject());
    }

    private JoinAreaRequest get(String areaId, String requestId) {
        return ofNullable(areaRepository.getJoinRequest(areaId, requestId))
                .orElseThrow(() -> new NotFoundException(requestId, JoinAreaRequest.class));
    }
}
