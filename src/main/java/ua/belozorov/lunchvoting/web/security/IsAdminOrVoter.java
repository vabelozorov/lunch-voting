package ua.belozorov.lunchvoting.web.security;

import org.springframework.security.access.annotation.Secured;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <h2></h2>
 *
 * Created on 20.02.17.
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Secured({"ROLE_VOTER", "ROLE_ADMIN"})
public @interface IsAdminOrVoter {
}
