package ua.belozorov.lunchvoting.repository.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ua.belozorov.lunchvoting.model.User;

import java.util.Collection;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 16.11.16.
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private CrudUserRepository crudRepository;


    @Override
    public User save(User user) {
        return crudRepository.save(user);
    }

    @Override
    public boolean update(User user) {
        return crudRepository.update(user.getId(), user.getName(), user.getEmail(), user.getPassword()) != 0;
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

    @Override
    public boolean activate(String id, boolean isActive) {
        return crudRepository.activate(id, isActive) != 0;
    }

    @Override
    public boolean setRoles(String id, byte bitmask) {
        return crudRepository.setRoles(id, bitmask) != 0;
    }
}
