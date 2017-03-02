package ua.belozorov.lunchvoting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

/**
 * Security Initializer class
 *
 * Created on 19.02.17.
 */

public class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {
    private final static Logger logger = LoggerFactory.getLogger(SecurityWebApplicationInitializer.class);

    public SecurityWebApplicationInitializer() {
        logger.debug("Initializing primary security class...");
    }
}
