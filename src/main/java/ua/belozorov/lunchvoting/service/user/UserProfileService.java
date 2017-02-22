package ua.belozorov.lunchvoting.service.user;

import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.repository.user.UserRepository;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 14.02.17.
 */
public interface UserProfileService {

    User register(User user);

    /**
     * Updates a user info with ID {@code id}. All parameters must be provided and will replace existing values
     *
     * @param id a existing user ID
     * @param name non-null, not-empty username
     * @param email non-null, not-empty user email
     * @param password non-null, not-empty user password
     * @throws ua.belozorov.lunchvoting.exceptions.NotFoundException if there is no entity with such ID
     */

    void updateMainInfo(String id, String name, String email, String password);

    /**
     * Updates a User entity by merging a given instance over its persisted version
     * @param user User instance which will override an existing entity
     * @throws ua.belozorov.lunchvoting.exceptions.NotFoundException if there is no entity with such ID
     */

    void update(User user);

    /**
     * Retrieves User entity from persistent storage.
     * This method is intended to be used for a user self-management as it doesn't contstrain the result
     * by specifying EatingArea#id
     * @param id existing non-null User#id
     * @return User entity
     * @throws ua.belozorov.lunchvoting.exceptions.NotFoundException if there is no entity with such ID
     */

    User get(String id);

    UserRepository getRepository();
}
