package ua.belozorov.lunchvoting.service.area;

import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.belozorov.lunchvoting.exceptions.NoAreaAdminException;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.UserRole;
import ua.belozorov.lunchvoting.model.lunchplace.EatingArea;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;
import ua.belozorov.lunchvoting.model.voting.polling.TimeConstraint;
import ua.belozorov.lunchvoting.repository.lunchplace.EatingAreaRepository;
import ua.belozorov.lunchvoting.repository.lunchplace.EatingAreaRepositoryImpl;
import ua.belozorov.lunchvoting.service.lunchplace.LunchPlaceService;
import ua.belozorov.lunchvoting.service.user.UserService;
import ua.belozorov.lunchvoting.service.voting.PollService;
import ua.belozorov.lunchvoting.to.AreaTo;
import ua.belozorov.lunchvoting.util.ExceptionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.util.Optional.ofNullable;

/**
 * <h2></h2>
 *
 * Created on 08.02.17.
 */
@Service
@Transactional(readOnly = true)
public final class EatingAreaServiceImpl implements EatingAreaService {

    private final EatingAreaRepository areaRepository;
    private final UserService userService;
    private final PollService pollService;

    @Autowired
    public EatingAreaServiceImpl(EatingAreaRepository areaRepository, UserService userService, PollService pollService) {
        this.areaRepository = areaRepository;
        this.userService = userService;
        this.pollService = pollService;
    }

    @Override
    @Transactional
    public EatingArea create(String name, User user) {
        if (user.belongsToArea()) {
            List<User> admins = userService.getUsersByRole(user.getAreaId(), UserRole.ADMIN);
            if (admins.equals(Collections.singletonList(user))) {
                throw new NoAreaAdminException();
            }
        }
        EatingArea area = areaRepository.save(new EatingArea(name));
        this.addMember(area.getId(), user);
        userService.setRoles(area.getId(), user.getId(), user.addRole(UserRole.ADMIN).getRoles());
        return area;
    }

    @Override
    @Transactional
    public User createUserInArea(String areaId, User user) {
        ExceptionUtils.checkParamsNotNull(areaId, user);

        User saved = userService.create(user);
        this.addMember(areaId, saved);
        return userService.getFresh(() -> userService.get(areaId, user.getId()));
    }

    @Override
    @Transactional
    public LunchPlace createPlaceInArea(String areaId, String id, String name, String address, String description,
                                        Set<String> phones) {
        EatingArea area = this.getArea(areaId, EatingAreaRepositoryImpl.Fields.PLACES);
        LunchPlace place = new LunchPlace(id, name, address, description, phones);
        areaRepository.update(area.addPlace(place));
        return place;
    }

    @Override
    @Transactional
    public LunchPlacePoll createPollInArea(String areaId, LocalDate menuDate, TimeConstraint timeConstraint) {
        ExceptionUtils.checkParamsNotNull(areaId, ExceptionUtils.NOT_CHECK, timeConstraint);

        LunchPlacePoll poll = pollService.createPollForMenuDate(areaId, menuDate, timeConstraint);
        EatingArea area = this.getArea(areaId, EatingAreaRepositoryImpl.Fields.POLLS);
        areaRepository.update(area.addPoll(poll));
        return poll;
    }

    @Override
    @Transactional
    public LunchPlacePoll createPollInArea(String areaId, LocalDate menuDate,
                                           LocalDateTime startTime,
                                           LocalDateTime endTime,
                                           LocalDateTime voteChangeTime) {
        ExceptionUtils.checkParamsNotNull(areaId);

        TimeConstraint timeConstraint = new TimeConstraint(startTime, endTime, voteChangeTime);
        return this.createPollInArea(areaId, menuDate, timeConstraint);
    }

    @Override
    @Transactional
    public void updateAreaName(String name, User user) {
        EatingArea area = this.getArea(user.getAreaId());
        areaRepository.update(area.changeName(name));
    }

    @Override
    public EatingArea getArea(String areaId) {
        return ofNullable(areaRepository.getArea(areaId))
                .orElseThrow(() -> new NotFoundException(areaId, EatingArea.class));
    }

    @Override
    public EatingArea getArea(String areaId, EatingAreaRepositoryImpl.Fields... fields) {
        return ofNullable(areaRepository.getArea(areaId, fields))
                .orElseThrow(() -> new NotFoundException(areaId, EatingArea.class));
    }

    @Override
    public AreaTo getAsTo(String areaId, boolean summary) {
        return ofNullable(summary ? areaRepository.getAreaToSummary(areaId) : areaRepository.getAreaTo(areaId))
                .orElseThrow(() -> new NotFoundException(areaId, EatingArea.class));
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
    public void addMember(String areaId, User member) {
        EatingArea area = this.getArea(areaId, EatingAreaRepositoryImpl.Fields.USERS);
        member = member.assignAreaId(area.getId());
        areaRepository.update(area.addMember(member));
    }

    @Override
    public EatingAreaRepository getRepository() {
        return this.areaRepository;
    }
}