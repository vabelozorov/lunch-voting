package ua.belozorov.lunchvoting.repository.lunchplace;

import ua.belozorov.lunchvoting.model.lunchplace.Menu;

import java.util.Collection;
import java.util.List;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 02.12.16.
 */
public interface MenuRepository {

    boolean deleteMenu(String lunchPlaceId, String menuId);

    Menu getMenu(String menuId);

    List<Menu> getTodayMenus();

    void save(Menu menu);
}
