package ua.belozorov.lunchvoting.service.user;

import org.springframework.transaction.annotation.Transactional;
import ua.belozorov.lunchvoting.model.User;

import java.util.Collection;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 15.11.16.
 */
//TODO save() and update() should not return values
public interface UserService {

    User get(String id);

    Collection<User> getAll();

    void delete(String id);

    void update(String id, String name, String email, String password);

    User create(User user);

    void activate(String id, boolean isActive);

    void setRoles(String id, byte bitmask);
}
