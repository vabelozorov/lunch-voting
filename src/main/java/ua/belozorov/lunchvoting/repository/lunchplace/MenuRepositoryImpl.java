package ua.belozorov.lunchvoting.repository.lunchplace;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Repository;
import ua.belozorov.lunchvoting.model.lunchplace.Menu;
import ua.belozorov.lunchvoting.repository.BaseRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static ua.belozorov.lunchvoting.util.Pair.pairOf;

/**
 * <h2></h2>
 *
 * Created on 02.12.16.
 */
@Repository
public class MenuRepositoryImpl extends BaseRepository implements MenuRepository  {

    @Override
    public Menu save(Menu menu) {
        em.persist(menu);
        return menu;
    }

    @Override
    public Menu get(String areaId, String placeId, String menuId, Fields... fields) {
        String sql = "SELECT m FROM EatingArea ea " +
                "INNER JOIN ea.places lp " +
                "INNER JOIN lp.menus m " +
                "WHERE ea.id= :areaId AND lp.id= :placeId AND m.id= :menuId";
        Menu menu = regularGet(sql, Menu.class,
                pairOf("areaId", areaId), pairOf("menuId", menuId), pairOf("placeId", placeId)
        );
        Set<Fields> fieldSet = new HashSet<>(Arrays.asList(fields));
        if (fieldSet.contains(Fields.DISHES)) {
            /* Unfortunately, this stupid bitch doesn't LEFT JOIN FETCH Menu#dishes,
             throwing "QueryException: query specified join fetching, but the owner of the fetched association was not present",
             so that is how it gets done
            */
            Hibernate.initialize(menu.getDishes());
        }
        return menu;
    }

    @Override
    public boolean delete(String menuId) {
        return em.createQuery("DELETE FROM Menu m WHERE m.id = :menuId")
                .setParameter("menuId", menuId)
                .executeUpdate() != 0;
    }

    public enum Fields {
        DISHES
    }
}
