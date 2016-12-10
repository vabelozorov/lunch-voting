package ua.belozorov.lunchvoting.service.lunchplace;

import org.springframework.transaction.annotation.Transactional;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.to.LunchPlaceTo;
import ua.belozorov.lunchvoting.to.MenuTo;

import java.util.Collection;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 21.11.16.
 */
public interface LunchPlaceService {

    LunchPlaceTo create(LunchPlaceTo placeTo, User user);

    //TODO update also returns a new object since we sorting a phones field
    void update(LunchPlaceTo placeTo, User user);

    LunchPlaceTo get(String id, User user);

    Collection<LunchPlaceTo> getAll(User user);

    void delete(String id, User user);

    MenuTo addMenu(String lunchPlaceId, MenuTo menuTo, User user);

    void deleteMenu(String lunchPlaceId, String menuId, User user);
}
