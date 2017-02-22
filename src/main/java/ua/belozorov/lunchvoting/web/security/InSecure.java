package ua.belozorov.lunchvoting.web.security;

import org.springframework.security.access.annotation.Secured;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that an applied target element does not require protection
 * by {@link Secured} annotation
 * @see ua.belozorov.lunchvoting.util.SecurityEnforcerBeanFactoryPostProcessor
 * @author vabelozorov on 20.02.17.
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface InSecure {
}
