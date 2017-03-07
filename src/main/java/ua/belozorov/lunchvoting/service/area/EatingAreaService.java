package ua.belozorov.lunchvoting.service.area;

import org.jetbrains.annotations.Nullable;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.area.EatingArea;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;
import ua.belozorov.lunchvoting.model.voting.polling.TimeConstraint;
import ua.belozorov.lunchvoting.repository.lunchplace.EatingAreaRepository;
import ua.belozorov.lunchvoting.repository.lunchplace.EatingAreaRepositoryImpl;
import ua.belozorov.lunchvoting.to.AreaTo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 *
 * Created on 08.02.17.
 */
public interface EatingAreaService {

    /**
     * Creates an EatingArea instance given an EatingArea name and a User instance. The user is granted with
     * ADMIN role. His area membership, if exists, is replaced with with a membership in a newly created area,
     * unless he is the only user with an ADMIN right in the area he currently belongs to. In the latter case
     * the operation fails
     * @param name
     * @param user
     * @return
     */
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

    LunchPlace createPlaceInArea(String areaId, @Nullable String id, String name, @Nullable String address, @Nullable String description,
                                 Set<String> phones);

    LunchPlacePoll createPollInArea(String areaId, LocalDate menuDate, TimeConstraint timeConstraint);

    LunchPlacePoll createPollInArea(String areaId, LocalDate menuDate,
                                    LocalDateTime startTime,
                                    LocalDateTime endTime,
                                    LocalDateTime voteChangeTime);

    void updateAreaName(String name, User user);

    EatingArea getArea(String areaId);

    EatingArea getArea(String areaId, EatingAreaRepositoryImpl.Fields... fields);

    AreaTo getAsTo(String areaId, boolean summary);

    List<EatingArea> filterByNameStarts(String search);

    void delete(String areaId);

    /**
     * Adds already persisted User {@code user} to an EatingArea {@code area}
     * EatingArea#users collection must be initialized
     * @param areaId persisted area
     * @param member persisted User entity
     */
    void addMember(String areaId, User member);

    EatingAreaRepository getRepository();
}
