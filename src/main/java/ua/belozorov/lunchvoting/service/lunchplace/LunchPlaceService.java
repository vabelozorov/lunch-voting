package ua.belozorov.lunchvoting.service.lunchplace;

import org.jetbrains.annotations.Nullable;
import ua.belozorov.lunchvoting.model.lunchplace.Dish;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.lunchplace.Menu;
import ua.belozorov.lunchvoting.repository.lunchplace.LunchPlaceRepository;
import ua.belozorov.lunchvoting.repository.lunchplace.MenuRepositoryImpl;
import ua.belozorov.lunchvoting.web.security.IsAdmin;
import ua.belozorov.lunchvoting.web.security.IsAdminOrVoter;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 21.11.16.
 */
public interface LunchPlaceService {

    LunchPlace create(LunchPlace placeTo);

    void bulkUpdate(String areaId, String placeId, @Nullable String name, @Nullable String address, @Nullable String description, @Nullable Set<String> phones);

    LunchPlace get(String areaId, String placeId);

    /**
     *
     *
     * @param areaId
     * @param placeIds
     * @param startDate
     * @param endDate
     * @return
     */
    List<LunchPlace> getMultipleWithMenu(String areaId, Set<String> placeIds, @Nullable LocalDate startDate, @Nullable LocalDate endDate);

    List<LunchPlace> getAll(String areaId);

    List<LunchPlace> getMultiple(String areaId, Set<String> placeIds);

    void delete(String areaId, String id);

    Menu addMenu(String areaId, String lunchPlaceId, LocalDate effectiveDate, Set<Dish> dishes);

    void deleteMenu(String areaId, String lunchPlaceId, String menuId);

    Menu getMenu(String areaId, String placeId, String menuId, MenuRepositoryImpl.Fields... fields);

    LunchPlaceRepository getRepository();
}
