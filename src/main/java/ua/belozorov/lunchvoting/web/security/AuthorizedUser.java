package ua.belozorov.lunchvoting.web.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ua.belozorov.lunchvoting.exceptions.AuthenticationException;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorCode;

import static java.util.Optional.ofNullable;

/**
 *
 * Created on 22.11.16.
 */
public class AuthorizedUser {

    public static User get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ofNullable(authentication)
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof User)
                .map(principal -> (User) principal)
                .orElseThrow(() -> new AuthenticationException(ErrorCode.AUTH_CREDENTIALS_NOT_FOUND));
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
            throw new AuthenticationException(ErrorCode.AUTH_AREA_NOT_ASSIGNED);
        }
        return areaId;
    }
}
