package ua.belozorov.lunchvoting.repository.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.repository.BaseRepository;

import java.util.Collection;
import java.util.List;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 16.11.16.
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
    public User get(String id) {
        return crudRepository.findOne(id);
    }

    @Override
    public Collection<User> getAll() {
        return crudRepository.findAll();
    }

    @Override
    public boolean delete(String id) {
        return crudRepository.deleteById(id) != 0;
    }

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
    }
}
