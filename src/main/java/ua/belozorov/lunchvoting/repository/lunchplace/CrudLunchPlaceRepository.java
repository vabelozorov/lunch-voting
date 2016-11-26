package ua.belozorov.lunchvoting.repository.lunchplace;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;

import java.util.Collection;
import java.util.List;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 21.11.16.
 */
public interface CrudLunchPlaceRepository extends JpaRepository<LunchPlace, String> {

    @Query("SELECT lp FROM LunchPlace lp JOIN FETCH lp.phones WHERE lp.id= :id and lp.admin.id= :userId")
    LunchPlace getOne(@Param("id") String id, @Param("userId")String userId);

    @Query("SELECT DISTINCT lp FROM LunchPlace lp JOIN FETCH lp.phones WHERE lp.admin.id= ?1 ORDER BY lp.name ASC")
    List<LunchPlace> findAll(String userId);

    @Modifying
    @Query("DELETE FROM LunchPlace lp WHERE lp.id= :id AND lp.admin.id= :userId")
    int deleteById(@Param("id") String id, @Param("userId")String userId);

//    @Modifying
//    @Query("UPDATE LunchPlace lp SET lp.name= :name, lp.address= :address, lp.description= :description, lp.phones= :phones " +
//            "WHERE lp.id= :id AND lp.admin.id= :userId")
//    int update(@Param("name")String name,
//               @Param("address")String address,
//               @Param("description")String description,
//               @Param("phones")Collection<String> phones,
//               @Param("id")String id,
//               @Param("userId")String userId);
}
