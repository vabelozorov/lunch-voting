package ua.belozorov.lunchvoting.repository.lunchplace;

import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**

 *
 * Created on 21.11.16.
 */
public interface LunchPlaceRepository {

    LunchPlace save(LunchPlace lunchPlace);

    void update(LunchPlace lunchPlace);

    LunchPlace get(String placeId);

    LunchPlace get(String areaId, String placeId);

    List<LunchPlace> getAll(String areaId);

    List<LunchPlace> getMultiple(String areaId, Set<String> placeIds);

    List<LunchPlace> getWithMenu(String areaId, Set<String> placeIds, LocalDate startDate, LocalDate endDate);

    boolean delete(String areaId, String placeId);

    List<LunchPlace> getIfMenuForDate(String areaId, LocalDate date);
}
