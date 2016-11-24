package ua.belozorov.lunchvoting.repository.lunchplace;

import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;

import java.util.Collection;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 21.11.16.
 */
public interface ILunchPlaceRepository {

    LunchPlace save(LunchPlace lunchPlace, String userId);

    boolean update(LunchPlace lunchPlace, String userId);

    LunchPlace get(String id, String userId);

    Collection<LunchPlace> getAll(String userId);

    boolean delete(String id, String userId);
}
