package ua.belozorov.lunchvoting.service.lunchplace;

import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;

import java.util.Collection;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 21.11.16.
 */
public interface ILunchPlaceService {

    LunchPlace create(LunchPlace place, String userId);

    void update(LunchPlace place, String userId);

    LunchPlace get(String id, String userId);

    Collection<LunchPlace> getAll(String userId);

    void delete(String id, String userId);

}
