package ua.belozorov.lunchvoting.repository.user;

import ua.belozorov.lunchvoting.model.User;

import java.util.Collection;

/**
 * Created by vabelozorov on 14.11.16.
 */

public interface UserRepository {

    User save(User user);

//    void update(String id, String name, String email, String passowrd);

    void update(User user);

    User get(String id);

    Collection<User> getAll();

    boolean delete(String id);
}
