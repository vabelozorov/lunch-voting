package ua.belozorov.lunchvoting.web;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import ua.belozorov.lunchvoting.mocks.ServiceMocks;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.service.user.UserProfileService;
import ua.belozorov.lunchvoting.to.UserTo;
import ua.belozorov.lunchvoting.util.ControllerUtils;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.belozorov.lunchvoting.model.UserTestData.*;
import static ua.belozorov.lunchvoting.model.UserTestData.VOTER;
import static ua.belozorov.lunchvoting.model.UserTestData.VOTER_ID;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 08.02.17.
 */
@ContextConfiguration(classes = {ServiceMocks.class})
public class UserProfileControllerTest extends AbstractControllerTest {
    public static final String REST_URL = UserProfileController.REST_URL;

    @Autowired
    private UserProfileService profileService;

    @Override
    public void beforeTest() {
    }

    @Test
    public void testRegister() throws Exception {
        UserTo userTo = new UserTo("New User", "new@email.com", "strongPassword");
        User newUser = new User(userTo.getName(), userTo.getEmail(), userTo.getPassword());

        when(profileService.register(userWithoutId(newUser))).thenReturn(newUser);
        MvcResult result = mockMvc
                .perform(
                        post(REST_URL)
                        .content(jsonUtils.toJson(userTo))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isCreated())
                .andReturn();
        verify(profileService).register(userWithoutId(newUser));

        String location = jsonUtils.locationFromMvcResult(result);
        String id = super.getCreatedId(location);
        String expected = jsonUtils.toJson(ControllerUtils.toMap("id", id));

        assertJson(expected, result.getResponse().getContentAsString());

        when(profileService.get(id)).thenReturn(newUser);
        mockMvc.perform(get(location).with(voter()).accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
    }

    @Test
    public void testUpdate() throws Exception {
        UserTo userTo = new UserTo(VOTER_ID, VOTER.getName(), "newEmail@email.com", "newPassword");

        mockMvc.perform(
                put(REST_URL)
                .content(jsonUtils.toJson(userTo))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(voter())
        )
        .andExpect(status().isNoContent());
        verify(profileService).updateMainInfo(VOTER_ID, VOTER.getName(), "newEmail@email.com", "newPassword");
    }

    @Test
    public void testGet() throws Exception {
        when(profileService.get(VOTER_ID)).thenReturn(VOTER);
        String actual = mockMvc
                .perform(
                        get(REST_URL + "/" + VOTER_ID)
                        .with(csrf())
                        .with(voter())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        verify(profileService).get(VOTER_ID);

        String expected = jsonUtils.toJson(new UserTo(VOTER));
        assertJson(expected, actual);
    }

    public static User userWithoutId(User user) {
        return argThat(new UserComparator(user, false, false));
    }
}