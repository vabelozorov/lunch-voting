package ua.belozorov.lunchvoting.service.user;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.belozorov.lunchvoting.WithMockVoter;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.service.AbstractServiceTest;

import static org.junit.Assert.*;
import static ua.belozorov.lunchvoting.MatcherUtils.matchSingle;
import static ua.belozorov.lunchvoting.model.UserTestData.USER_COMPARATOR;
import static ua.belozorov.lunchvoting.model.UserTestData.VOTER;
import static ua.belozorov.lunchvoting.model.UserTestData.VOTER_ID;

/**

 *
 * Created on 21.02.17.
 */
@WithMockVoter
public class UserProfileServiceImplTest extends AbstractServiceTest {

    @Autowired
    private UserProfileService profileService;

    @Test
    public void register() throws Exception {
        User newUser = new User("New name", "email@mail.com", "newpassword");
        User expected = profileService.register(newUser);
        User actual = profileService.get(expected.getId());

        assertThat(actual, matchSingle(expected, USER_COMPARATOR));
    }

    @Test
    @WithMockVoter
    public void updateMainInfo() throws Exception {
        profileService.updateMainInfo(VOTER_ID, "UpdatedName", VOTER.getEmail(), "UpdatedPassword");
        User actual = profileService.get(VOTER_ID);
        User expected = VOTER.toBuilder().name("UpdatedName").password("UpdatedPassword").build();

        assertThat(actual, matchSingle(expected, USER_COMPARATOR));
    }

    @Test
    @WithMockVoter
    public void update() throws Exception {
        User voter = profileService.get(VOTER_ID);
        User expected = voter.toBuilder().name("UpdatedName").password("UpdatedPassword").build();
        profileService.update(expected);
        User actual = profileService.get(VOTER_ID);

        assertThat(actual, matchSingle(expected, USER_COMPARATOR));
    }

    @Test
    @WithMockVoter
    public void get() throws Exception {
        User actual = profileService.get(VOTER_ID);

        assertThat(actual, matchSingle(VOTER, USER_COMPARATOR));
    }
}