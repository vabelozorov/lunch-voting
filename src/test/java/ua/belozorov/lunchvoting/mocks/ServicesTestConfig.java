package ua.belozorov.lunchvoting.mocks;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import ua.belozorov.lunchvoting.model.lunchplace.JoinAreaRequest;
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
 * <h2></h2>
 *
 * Created on 22.02.17.
 */
@Configuration
public class ServicesTestConfig {

    @Bean
    public UserDetailsService userDetailsService(UserRepository repository) {
        return new UserServiceImpl(repository);
    }

    @Bean
    public UserProfileService userProfileService() {
        return mock(UserProfileService.class);
    }

    @Bean
    public UserService userService() {
        return mock(UserService.class);
    }

    @Bean
    public PollService pollService() {
        return mock(PollService.class);
    }

    @Bean
    public VotingService votingService() {
        return mock(VotingService.class);
    }

    @Bean
    public LunchPlaceService lunchPlaceService() {
        return mock(LunchPlaceService.class);
    }

    @Bean
    public JoinAreaRequestService joinAreaRequestService() {
        return mock(JoinAreaRequestService.class);
    }

    @Bean
    public EatingAreaService eatingAreaService() {
        return mock(EatingAreaService.class);
    }
}
