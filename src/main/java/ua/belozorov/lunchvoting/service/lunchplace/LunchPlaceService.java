package ua.belozorov.lunchvoting.service.lunchplace;

import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.to.LunchPlaceTo;

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

}
