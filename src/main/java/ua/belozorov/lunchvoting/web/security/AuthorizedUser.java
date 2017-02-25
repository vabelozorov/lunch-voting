package ua.belozorov.lunchvoting.web.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ua.belozorov.lunchvoting.model.User;

/**
 *
 * Created on 22.11.16.
 */
public class AuthorizedUser {

    public static User get() {
        return get(true);
    }

    public static User get(boolean requiresNonNull) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (requiresNonNull && authentication == null) {
            throw new IllegalStateException("Authentication object expected, but got none");
        }
        User principal = (User) authentication.getPrincipal();
        if (requiresNonNull && principal == null) {
            throw new IllegalStateException("Authenticated user expected, but got none");
        }
        return principal;
    }

    public static String getId() {
        return get().getId();
    }

    public static String getAreaId() {
        return getAreaId(true);
    }

    public static String getAreaId(boolean requiresNotNull) {
        String areaId = get().getAreaId();
        if ( requiresNotNull && areaId == null) {
            throw new IllegalStateException("User" + getId() + " is not assigned to any area, but method expects this");
        }
        return areaId;
    }
}
