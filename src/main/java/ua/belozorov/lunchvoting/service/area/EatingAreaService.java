package ua.belozorov.lunchvoting.service.area;

import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.lunchplace.EatingArea;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;
import ua.belozorov.lunchvoting.repository.lunchplace.EatingAreaRepository;
import ua.belozorov.lunchvoting.repository.lunchplace.EatingAreaRepositoryImpl;
import ua.belozorov.lunchvoting.to.AreaTo;
import ua.belozorov.lunchvoting.web.security.IsAdmin;
import ua.belozorov.lunchvoting.web.security.IsAdminOrVoter;

import java.time.LocalDate;
import java.util.List;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 08.02.17.
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
    @IsAdminOrVoter
    EatingArea create(String name, User user);

    /**
     * Saves a new User instance and assigns it to Eating area signified by {@code areaId} parameter.
     * User's ID, email, password and name are provided by {@code user} parameter
     * and other values are set to default values
     * @param user non-null user instance
     * @param areaId non-null ID of existing EatingArea entity
     * @return persisted User instance
     */

    @IsAdmin
    User createUserInArea(String areaId, User user);

    @IsAdmin
    LunchPlace createPlaceInArea(String areaId, LunchPlace place);

    @IsAdmin
    LunchPlacePoll createPollInArea(String areaId);

    @IsAdmin
    LunchPlacePoll createPollInArea(String areaId, LocalDate menuDate);

    @IsAdmin
    void updateAreaName(String name, User user);

    @IsAdminOrVoter
    EatingArea getArea(String areaId);

    @IsAdminOrVoter
    EatingArea getArea(String areaId, EatingAreaRepositoryImpl.Fields... fields);

    @IsAdminOrVoter
    AreaTo getAsTo(String areaId, boolean summary);

    @IsAdminOrVoter
    List<EatingArea> filterByNameStarts(String search);

    @IsAdmin
    void delete(String areaId);

    /**
     * Adds already persisted User {@code user} to an EatingArea {@code area}
     * EatingArea#users collection must be initialized
     * @param areaId persisted area
     * @param member persisted User entity
     */
    @IsAdmin
    void addMember(String areaId, User member);

    EatingAreaRepository getRepository();
}
