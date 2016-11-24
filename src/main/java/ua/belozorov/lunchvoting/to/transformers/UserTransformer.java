package ua.belozorov.lunchvoting.to.transformers;

import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.UserRole;
import ua.belozorov.lunchvoting.to.UserTo;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 15.11.16.
 */
public class UserTransformer {

    public static User toEntity(UserTo userTo) {
        User user = new User(userTo.getId(), userTo.getName(), userTo.getEmail(), userTo.getPassword());
        return user;
    }

    public static UserTo toDto(User user) {
        return new UserTo(user.getId(), user.getName(), user.getEmail(), "", user.getRoles(),
                user.getRegisteredDate(), user.isActivated());
    }
}
