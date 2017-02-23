package ua.belozorov.lunchvoting.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import ua.belozorov.lunchvoting.repository.lunchplace.EatingAreaRepository;
import ua.belozorov.lunchvoting.repository.lunchplace.LunchPlaceRepository;
import ua.belozorov.lunchvoting.repository.lunchplace.MenuRepository;
import ua.belozorov.lunchvoting.repository.user.UserRepository;
import ua.belozorov.lunchvoting.repository.voting.PollRepository;
import ua.belozorov.lunchvoting.service.area.EatingAreaService;
import ua.belozorov.lunchvoting.service.area.EatingAreaServiceImpl;
import ua.belozorov.lunchvoting.service.area.JoinAreaRequestService;
import ua.belozorov.lunchvoting.service.area.JoinAreaRequestServiceImpl;
import ua.belozorov.lunchvoting.service.lunchplace.LunchPlaceService;
import ua.belozorov.lunchvoting.service.lunchplace.LunchPlaceServiceImpl;
import ua.belozorov.lunchvoting.service.user.UserProfileService;
import ua.belozorov.lunchvoting.service.user.UserProfileServiceImpl;
import ua.belozorov.lunchvoting.service.user.UserService;
import ua.belozorov.lunchvoting.service.user.UserServiceImpl;
import ua.belozorov.lunchvoting.service.voting.PollService;
import ua.belozorov.lunchvoting.service.voting.PollServiceImpl;
import ua.belozorov.lunchvoting.service.voting.VotingService;
import ua.belozorov.lunchvoting.service.voting.VotingServiceImpl;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 23.02.17.
 */
@Configuration
@ComponentScan(basePackages = {
        "ua.belozorov.lunchvoting.service"
})
public class ServiceBeansConfig {

}
