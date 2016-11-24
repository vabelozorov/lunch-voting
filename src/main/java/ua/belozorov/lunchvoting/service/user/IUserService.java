package ua.belozorov.lunchvoting.service.user;

import ua.belozorov.lunchvoting.model.User;

import java.util.Collection;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 15.11.16.
 */
public interface IUserService {

    User get(String id);

    Collection<User> getAll();

    void delete(String id);

    void update(User user);

    User create(User user);

    void activate(String id, boolean isActive);

    void setRoles(String id, byte bitmask);
}
