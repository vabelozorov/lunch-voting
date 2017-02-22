package ua.belozorov.lunchvoting.mocks;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ua.belozorov.lunchvoting.service.user.UserProfileService;
import ua.belozorov.lunchvoting.service.user.UserService;

import static org.mockito.Mockito.mock;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 22.02.17.
 */
@Configuration
public class LocalTestConfig {

    @Bean
    @Primary
    public UserProfileService userProfileService() {
        return mock(UserProfileService.class);
    }

    @Bean
    @Primary
    public UserService userService() {
        return mock(UserService.class);
    }
}
