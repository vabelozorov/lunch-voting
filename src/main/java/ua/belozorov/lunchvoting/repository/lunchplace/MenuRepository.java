package ua.belozorov.lunchvoting.repository.lunchplace;

import ua.belozorov.lunchvoting.model.lunchplace.Menu;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 02.12.16.
 */
public interface MenuRepository {

    Menu save(Menu menu);

    Menu getMenu(String areaId, String placeId, String menuId, MenuRepositoryImpl.Fields... fields);

    boolean deleteMenu(String menuId);
}
