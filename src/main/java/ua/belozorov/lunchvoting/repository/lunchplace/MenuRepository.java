package ua.belozorov.lunchvoting.repository.lunchplace;

import ua.belozorov.lunchvoting.model.lunchplace.Menu;

/**
 * <h2></h2>
 *
 * Created on 02.12.16.
 */
public interface MenuRepository {

    Menu save(Menu menu);

    Menu get(String areaId, String placeId, String menuId, MenuRepositoryImpl.Fields... fields);

    boolean delete(String menuId);
}
