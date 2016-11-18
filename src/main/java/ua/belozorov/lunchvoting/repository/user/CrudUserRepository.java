package ua.belozorov.lunchvoting.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ua.belozorov.lunchvoting.model.User;

import java.util.List;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 16.11.16.
 */
public interface CrudUserRepository extends JpaRepository<User, String> {

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
