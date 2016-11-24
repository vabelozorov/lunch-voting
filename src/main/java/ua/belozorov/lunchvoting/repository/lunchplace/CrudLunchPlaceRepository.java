package ua.belozorov.lunchvoting.repository.lunchplace;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;

import java.util.List;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 21.11.16.
 */
public interface CrudLunchPlaceRepository extends JpaRepository<LunchPlace, String> {

    @Query("SELECT lp FROM LunchPlace lp JOIN FETCH lp.phones WHERE lp.id= ?1 and lp.admin.id= ?2")
    LunchPlace getOne(String id, String userId);

    @Query("SELECT DISTINCT lp FROM LunchPlace lp JOIN FETCH lp.phones WHERE lp.admin.id= ?1 ORDER BY lp.name ASC")
    List<LunchPlace> findAll(String userId);

    @Modifying
    @Query("DELETE FROM LunchPlace lp WHERE lp.id= ?1 AND lp.admin.id=?2")
    int deleteById(String id, String userId);
}
