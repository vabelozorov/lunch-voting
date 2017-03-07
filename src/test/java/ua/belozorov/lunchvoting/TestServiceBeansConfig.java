package ua.belozorov.lunchvoting;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import ua.belozorov.lunchvoting.repository.user.UserRepository;
import ua.belozorov.lunchvoting.service.area.EatingAreaService;
import ua.belozorov.lunchvoting.service.area.JoinAreaRequestService;
import ua.belozorov.lunchvoting.service.lunchplace.LunchPlaceService;
import ua.belozorov.lunchvoting.service.user.UserProfileService;
import ua.belozorov.lunchvoting.service.user.UserService;
import ua.belozorov.lunchvoting.service.user.UserServiceImpl;
import ua.belozorov.lunchvoting.service.voting.PollService;
import ua.belozorov.lunchvoting.service.voting.VotingService;

import static org.mockito.Mockito.mock;

/**

 *
 * Created on 22.02.17.
 */
@Configuration
public class TestServiceBeansConfig {

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        return mock(UserDetailsService.class);
    }

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

    @Bean
    @Primary
    public PollService pollService() {
        return mock(PollService.class);
    }

    @Bean
    @Primary
    public VotingService votingService() {
        return mock(VotingService.class);
    }

    @Bean
    @Primary
    public LunchPlaceService lunchPlaceService() {
        return mock(LunchPlaceService.class);
    }

    @Bean
    @Primary
    public JoinAreaRequestService joinAreaRequestService() {
        return mock(JoinAreaRequestService.class);
    }

    @Bean
    @Primary
    public EatingAreaService eatingAreaService() {
        return mock(EatingAreaService.class);
//        return mock(EatingAreaService.class, withSettings().verboseLogging());
    }
}
