package ua.belozorov.lunchvoting.service.lunchplace;

import org.springframework.transaction.annotation.Transactional;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.to.LunchPlaceTo;
import ua.belozorov.lunchvoting.to.MenuTo;

import java.util.Collection;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 21.11.16.
 */
public interface LunchPlaceService {

    String create(LunchPlace placeTo, User user);

    void update(LunchPlace placeTo, User user);

    LunchPlace get(String id, User user);

    Collection<LunchPlace> getAll(User user);

    void delete(String id, User user);

    String addMenu(String lunchPlaceId, MenuTo menuTo, User user);

    void deleteMenu(String lunchPlaceId, String menuId, User user);
}
