package ua.belozorov.lunchvoting.service.user;

import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.UserRole;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**

 *
 * Created on 15.11.16.
 */
public interface UserService {

    /**
     * Saves a new User instance to a persistent storage
     * @param user non-null user instance
     * @return saved user object
     * @throws org.springframework.dao.DataIntegrityViolationException if User#email already exists
     */
    User create(User user);

    /**
     * Updates a user info with ID {@code id}. All parameters must be provided and will replace existing values
     *
     * @param areaId EatingArea#id which the user to be updated belongs to
     * @param id a existing user ID
     * @param name non-null, not-empty username
     * @param email non-null, not-empty user email
     * @param password non-null, not-empty user password
     * @throws ua.belozorov.lunchvoting.web.exceptionhandling.exceptions.NotFoundException if there is no entity with such ID
     * in the specified area
     */
    void updateMainInfo(String areaId, String id, String name, String email, String password);

    /**
     * Updates a User entity by merging a given instance over its persisted version
     * @param areaId
     * @param user User instance which will override an existing entity
     * @throws ua.belozorov.lunchvoting.web.exceptionhandling.exceptions.NotFoundException if there is no entity with such ID
     * in the specified area
     */
    void update(String areaId, User user);

    /**
     * Retrieves User entity from persistent storage.
     * This method is intended to be used for an administrative management as it constrains the result
     * by specifying EatingArea#id
     * @param areaId existing non-null EatingArea#id
     * @param userId existing non-null User#id
     * @return User entity
     * @throws ua.belozorov.lunchvoting.web.exceptionhandling.exceptions.NotFoundException if there is no entity with such ID
     * in the specified area
     */
    User get(String areaId, String userId);

    Collection<User> getAll(String areaId);

    void delete(String areaId, String id);

    void activate(String areaId, String userId, boolean isActive);

    void setRoles(String areaId, String userId, Set<UserRole> roles);

    /**
     * Returns result of executing {@code supplier} after flushing and clearing a persistent context
     * @param supplier a method to execute after persistent context has been cleared.
     * @return User entity as a result of executing the {@code supplier}
     */
    User getFresh(Supplier<User> supplier);

    List<User> getUsersByRole(String areaId, UserRole role);
}
