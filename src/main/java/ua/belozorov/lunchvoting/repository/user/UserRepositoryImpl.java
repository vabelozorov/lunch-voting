package ua.belozorov.lunchvoting.repository.user;

import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.UserRole;
import ua.belozorov.lunchvoting.repository.BaseRepository;
import ua.belozorov.lunchvoting.util.Pair;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**

 *
 * Created on 16.11.16.
 */
@Repository
public class UserRepositoryImpl extends BaseRepository implements UserRepository {

    @Autowired
    private CrudUserRepository crudRepository;

    @Override
    public User save(User user) {
        return crudRepository.save(user);
    }

    @Override
    public void update(User user) {
        crudRepository.save(user);
    }

    @Override
    public User get(@Nullable String areaId, String userId) {
        return areaId == null ? crudRepository.findOne(userId)
                : crudRepository.findOneByAreaIdAndId(areaId, userId);
    }

    @Override
    public Collection<User> getAll(@Nullable String areaId) {
        return areaId == null ? crudRepository.findAll()
                : crudRepository.findAll(areaId);
    }

    @Override
    public boolean delete(String areaId, String userId) {
        return crudRepository.deleteById(areaId, userId) != 0;
    }


    @Override
    public List<User> getUsersByRole(String areaId, UserRole role) {
        String sql = "SELECT u FROM User u WHERE u.areaId= :areaId AND bitwise_and(u.roles, :mask) != 0";
        return super.regularGetList(sql, User.class, new Pair<>("areaId", areaId), new Pair<>("mask", 1 << role.ordinal()));
    }

    @Override
    public Optional<User> geByEmail(String email) {
        return crudRepository.getByEmail(email);
    }

    /**
     * <h2></h2>
     *
     * Created on 16.11.16.
     */
    public interface CrudUserRepository extends JpaRepository<User, String> {

        @Override
        @Query("SELECT u FROM User u ORDER BY u.email ASC")
        List<User> findAll();

        @Query("SELECT u FROM User u WHERE u.areaId= ?1 ORDER BY u.email ASC")
        List<User> findAll(String areaId);


        @Modifying
        @Query("DELETE FROM User u WHERE u.areaId= :areaId AND u.id = :userId")
        int deleteById(@Param("areaId") String areaId, @Param("userId") String userId);

        User findOneByAreaIdAndId(String areaId, String userId);

        Optional<User> getByEmail(String email);
    }
}
