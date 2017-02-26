package ua.belozorov.lunchvoting.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import ua.belozorov.lunchvoting.mocks.ServicesTestConfig;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.service.user.UserProfileService;
import ua.belozorov.lunchvoting.to.UserTo;
import ua.belozorov.lunchvoting.util.ControllerUtils;

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

 *
 * Created on 08.02.17.
 */
public class UserProfileControllerTest extends AbstractControllerTest {
    public static final String REST_URL = UserProfileController.REST_URL;

    @Autowired
    private UserProfileService profileService;

    @Before
    public void setUp() throws Exception {
        Mockito.reset(profileService);
    }

    @Test
    public void newUserRegisters() throws Exception {
        UserTo userTo = new UserTo("New User", "new@email.com", "strongPassword");
        User newUser = new User(userTo.getName(), userTo.getEmail(), userTo.getPassword());

        when(profileService.register(userNoIdNoDate(newUser))).thenReturn(newUser);
        MvcResult result = mockMvc
                .perform(
                        post(REST_URL)
                        .content(jsonUtils.toJson(userTo))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isCreated())
                .andReturn();
        verify(profileService).register(userNoIdNoDate(newUser));

        String location = jsonUtils.locationFromMvcResult(result);
        String id = super.getCreatedId(location);

        mockMvc.perform(get(location).with(voter()).accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
    }

    @Test
    public void testUpdate() throws Exception {
        UserTo userTo = new UserTo(VOTER.getName(), "newEmail@email.com", "newPassword");

        mockMvc.perform(
                put(REST_URL + "/{userId}", VOTER_ID)
                .content(jsonUtils.toJson(userTo))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(voter())
        )
        .andExpect(status().isNoContent());
        verify(profileService).updateMainInfo(VOTER_ID, VOTER.getName(), "newEmail@email.com", "newPassword");
    }

    @Test
    public void getUser() throws Exception {
        String actual = mockMvc
                .perform(
                        get(REST_URL)
                        .with(csrf())
                        .with(voter())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String expected = jsonUtils.toJson(new UserTo(VOTER));
        assertJson(expected, actual);
    }
}