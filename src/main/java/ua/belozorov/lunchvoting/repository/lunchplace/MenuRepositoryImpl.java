package ua.belozorov.lunchvoting.repository.lunchplace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.belozorov.lunchvoting.model.lunchplace.Menu;
import ua.belozorov.lunchvoting.repository.BaseRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 02.12.16.
 */
@Repository
public class MenuRepositoryImpl extends BaseRepository implements MenuRepository  {

    @Autowired
    private CrudMenuRepository repository;

    @Override
    public void save(Menu menu) {
        reliablePersist(menu);
    }

    @Override
    public Menu getMenu(String menuId) {
        return em.find(Menu.class, menuId);
    }

    @Override
    public List<Menu> getTodayMenus() {
        List<Menu> menus = repository.getAllMenusOfDate(LocalDate.now());
        return menus == null ? Collections.emptyList() : menus;
    }

    @Override
    public boolean deleteMenu(String lunchPlaceId, String menuId) {
        return repository.deleteMenu(lunchPlaceId, menuId) != 0;
    }

    /**
     * Spring Data JPA repository for Menu entities
     */
    public interface CrudMenuRepository extends JpaRepository<Menu, String> {
        @Query("SELECT m FROM Menu m JOIN FETCH m.lunchPlace WHERE m.lunchPlace.id= :lunchPlaceId AND m.id= :menuId AND m.lunchPlace.adminId= :userId")
        Menu getMenu(@Param("lunchPlaceId") String lunchPlaceId,
                     @Param("menuId") String menuId,
                     @Param("userId") String userId);

        @Query("SELECT m FROM Menu m JOIN FETCH m.lunchPlace WHERE m.effectiveDate = ?1")
        List<Menu> getAllMenusOfDate(LocalDate date);

        @Modifying
        @Query("DELETE FROM Menu m WHERE m.id= :menuId AND m.lunchPlace.id= :lunchPlaceId")
        int deleteMenu(@Param("lunchPlaceId") String lunchPlaceId,
                       @Param("menuId") String menuId);
    }
}
