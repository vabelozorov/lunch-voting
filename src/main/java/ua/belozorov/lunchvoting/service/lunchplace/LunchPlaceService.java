package ua.belozorov.lunchvoting.service.lunchplace;

import org.jetbrains.annotations.Nullable;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.to.MenuTo;

import java.time.LocalDate;
import java.util.Collection;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 21.11.16.
 */
public interface LunchPlaceService {

    LunchPlace create(LunchPlace placeTo, User user);

    void update(LunchPlace placeTo, User user);

    LunchPlace get(String id, User user);

    /**
     *
     * @param placeIds
     * @param startDate
     * @param endDate
     * @param user
     * @return
     */
    Collection<LunchPlace> getMultipleWithMenu(Collection<String> placeIds, @Nullable LocalDate startDate, @Nullable LocalDate endDate, User user);

    Collection<LunchPlace> getAll(User user);

    Collection<LunchPlace> getMultiple(Collection<String> placeIds, User user);

    void delete(String id, User user);

    String addMenu(String lunchPlaceId, MenuTo menuTo, User user);

    void deleteMenu(String lunchPlaceId, String menuId, User user);
}
