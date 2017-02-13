package ua.belozorov.lunchvoting.service.lunchplace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.UserRole;
import ua.belozorov.lunchvoting.model.lunchplace.EatingArea;
import ua.belozorov.lunchvoting.model.lunchplace.JoinAreaRequest;
import ua.belozorov.lunchvoting.repository.lunchplace.EatingAreaRepository;
import ua.belozorov.lunchvoting.service.user.UserService;
import ua.belozorov.lunchvoting.to.AreaTo;

import java.util.List;

import static java.util.Optional.ofNullable;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 08.02.17.
 */
@Service
@Transactional(readOnly = true)
public class EatingAreaServiceImpl implements EatingAreaService {

    private final EatingAreaRepository areaRepository;
    private final UserService userService;

    @Autowired
    public EatingAreaServiceImpl(EatingAreaRepository areaRepository, UserService userService) {
        this.areaRepository = areaRepository;
        this.userService = userService;
    }

    @Override
    @Transactional
    public EatingArea create(String name, User user) {
        userService.setRoles(user.getId(), user.addRole(UserRole.ADMIN).getRoles());
        return areaRepository.save(new EatingArea(name, user));
    }

    @Override
    @Transactional
    public void update(String name, User user) {
        EatingArea area = this.getArea(user.getAreaId());
        areaRepository.update(area.changeName(name));
    }

    @Override
    public EatingArea getArea(String areaId) {
        return ofNullable(areaRepository.getArea(areaId))
                .orElseThrow(() -> new NotFoundException(areaId, EatingArea.class));
    }

    @Override
    public AreaTo getAsTo(String areaId, boolean summary) {
        return ofNullable(summary ? areaRepository.getAreaToSummary(areaId) : areaRepository.getAreaTo(areaId))
                .orElseThrow(() -> new NotFoundException(areaId, EatingArea.class));
    }

    @Override
    public JoinAreaRequest getJoinRequest(String areaId, String requestId) {
        return ofNullable(areaRepository.getJoinRequest(areaId, requestId))
                .orElseThrow(() -> new NotFoundException(requestId, JoinAreaRequest.class));
    }

    @Override
    public List<JoinAreaRequest> getJoinRequestsByStatus(String areaId, JoinAreaRequest.JoinStatus status) {
        return areaRepository.getJoinRequestsByStatus(areaId, status);
    }

    @Override
    public List<JoinAreaRequest> getJoinRequestsByRequester(User user) {
        return areaRepository.getJoinRequestsByRequester(user.getId());
    }

    @Override
    public JoinAreaRequest getJoinRequestByRequester(User user, String requestId) {
        return ofNullable(areaRepository.getJoinRequestByRequester(user.getId(), requestId))
                .orElseThrow(() -> new NotFoundException(requestId, JoinAreaRequest.class));
    }

    @Override
    public List<EatingArea> filterByNameStarts(String search) {
        return areaRepository.getByNameStarts(search);
    }

    @Override
    @Transactional
    public void delete(String areaId) {
        if ( ! areaRepository.delete(areaId) ) {
            throw new NotFoundException(areaId, EatingArea.class);
        };
    }

    @Override
    @Transactional
    public JoinAreaRequest makeJoinRequest(User requester, String areaId) {
        EatingArea area = areaRepository.getArea(areaId);
        JoinAreaRequest request = new JoinAreaRequest(requester, area);
        return areaRepository.save(request);
    }

    @Override
    @Transactional
    public void approveJoinRequest(User approver, String requestId) {
        String areaId = approver.getAreaId();
        JoinAreaRequest request = this.getJoinRequest(areaId, requestId);
        areaRepository.update(request.approve());

        EatingArea area = request.getArea();
        areaRepository.update(area.join(request));
    }

    @Override
    @Transactional
    public void cancelJoinRequest(User requester, String requestId) {
        JoinAreaRequest request = this.getJoinRequestByRequester(requester, requestId);
        areaRepository.update(request.cancel());
    }

    @Override
    @Transactional
    public void rejectJoinRequest(User approver, String requestId) {
        JoinAreaRequest request = this.getJoinRequest(approver.getAreaId(), requestId);
        areaRepository.update(request.reject());
    }
}
