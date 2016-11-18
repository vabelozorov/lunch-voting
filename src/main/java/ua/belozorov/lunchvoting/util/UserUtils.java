package ua.belozorov.lunchvoting.util;

import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.UserRole;
import ua.belozorov.lunchvoting.to.UserTo;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 15.11.16.
 */
public class UserUtils {

    public static UserTo convertIntoTo(User user) {
        return new UserTo(user.getId(), user.getName(), user.getEmail(), user.getRoles(),
                user.getRegisteredDate(), user.isActivated());
    }

    public static User convertIntoUser(UserTo userTo) {
        User user = new User(userTo.getName(), userTo.getEmail(), userTo.getPassword());
        user.setId(userTo.getId());
        return user;
    }
}
