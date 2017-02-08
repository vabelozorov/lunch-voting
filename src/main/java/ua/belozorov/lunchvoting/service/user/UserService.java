package ua.belozorov.lunchvoting.service.user;

import org.springframework.transaction.annotation.Transactional;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.UserRole;

import java.util.Collection;
import java.util.Set;

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

    /**
     * Updates a user info with ID {@code id}. All parameters must be provided and will replace existing values
     * @param id a existing user ID
     * @param name non-null, not-empty username
     * @param email non-null, not-empty user email
     * @param password non-null, not-empty user password
     */
    void update(String id, String name, String email, String password);

    /**
     * Saves a new User instance where an ID, an email, a password and a name are provided by {@code user}
     * parameter and other values are set to default values
     * @param user non-null user instance
     * @return
     */
    User create(User user);

    void activate(String id, boolean isActive);

    void setRoles(String id, Set<UserRole> roles);
}
