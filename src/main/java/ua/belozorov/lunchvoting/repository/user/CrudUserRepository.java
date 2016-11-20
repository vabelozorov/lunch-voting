package ua.belozorov.lunchvoting.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ua.belozorov.lunchvoting.model.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 16.11.16.
 */
public interface CrudUserRepository extends JpaRepository<User, String> {

    @Modifying
    @Query("UPDATE User u set u.name= :name, u.email= :email, u.password= :password WHERE u.id= :id")
    int update(@Param("id")String id,
               @Param("name")String name,
               @Param("email")String email,
               @Param("password")String password);

    @Override
    @Query("SELECT u FROM User u ORDER BY u.email ASC")
    List<User> findAll();

    @Modifying
    @Query("DELETE FROM User u WHERE u.id = ?1")
    int deleteById(String id);

    @Modifying
    @Query("UPDATE User u SET u.activated = ?2 WHERE u.id = ?1")
    int activate(String id, boolean isActive);

    @Modifying
    @Query("UPDATE User u SET u.roles = ?2 WHERE u.id = ?1")
    int setRoles(String id, byte bitmask);
}
