package ua.belozorov.lunchvoting.repository.lunchplace;

import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.lunchplace.Menu;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 21.11.16.
 */
public interface LunchPlaceRepository {

    void save(LunchPlace lunchPlace);

    void update(LunchPlace lunchPlace, String userId);

    LunchPlace get(String id, String userId);

    Collection<LunchPlace> getAll(String userId);

    Collection<LunchPlace> getMultiple(Collection<String> placeIds);

    Collection<LunchPlace> getWithMenu(Collection<String> placeIds, LocalDate startDate, LocalDate endDate);

    boolean delete(String id, String userId);

    List<LunchPlace> getByMenusForDate(LocalDate date);
}
