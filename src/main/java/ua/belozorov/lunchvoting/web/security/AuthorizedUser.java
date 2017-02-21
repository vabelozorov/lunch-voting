package ua.belozorov.lunchvoting.web.security;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.UserRole;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 22.11.16.
 */
public class AuthorizedUser {

    private static User authorizedUser;
    public static void authorize(User user) {
//        authorizedUser = user;
    }

    public static User get() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
