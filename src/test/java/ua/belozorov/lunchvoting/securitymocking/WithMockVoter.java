package ua.belozorov.lunchvoting.securitymocking;

import org.springframework.security.test.context.support.WithMockUser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**

 *
 * Created on 20.02.17.
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@WithMockUser(roles = {"VOTER"})
public @interface WithMockVoter {
}
