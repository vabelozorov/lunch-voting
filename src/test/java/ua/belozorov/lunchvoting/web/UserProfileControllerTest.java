package ua.belozorov.lunchvoting.web;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.service.user.UserService;
import ua.belozorov.lunchvoting.to.UserTo;
import ua.belozorov.lunchvoting.util.ControllerUtils;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.belozorov.lunchvoting.model.UserTestData.VOTER;
import static ua.belozorov.lunchvoting.model.UserTestData.VOTER_ID;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 08.02.17.
 */
public class UserProfileControllerTest extends AbstractControllerTest {
    public static final String REST_URL = UserProfileController.REST_URL;

    @Autowired
    private UserService userService;

    @Test
    public void testRegister() throws Exception {
        UserTo userTo = new UserTo("New User", "new@email.com", "strongPassword");
        MvcResult result = mockMvc
                .perform(post(REST_URL)
                        .content(jsonUtils.toJson(userTo))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        String location = jsonUtils.locationFromMvcResult(result);
        String id = super.getCreatedId(location);
        String expected = jsonUtils.toJson(ControllerUtils.toMap("id", id));

        assertJson(expected, result.getResponse().getContentAsString());

        String got = mockMvc.perform(get("{base}/{id}", REST_URL, id).accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        UserTo to = jsonUtils.strToObject(got, UserTo.class);

        assertTrue(to.getId().equals(id));
    }

    @Test
    public void testUpdate() throws Exception {
        UserTo userTo = new UserTo(VOTER_ID, VOTER.getName(), "newEmail@email.com", "newPassword");

        mockMvc.perform(put(REST_URL)
                .content(jsonUtils.toJson(userTo))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        User user = userService.get(VOTER_ID);

        assertEquals(userTo.getPassword(), user.getPassword());
        assertEquals(userTo.getEmail(), user.getEmail());
    }

    @Test
    public void testGet() throws Exception {
        String actual = mockMvc.perform(get(REST_URL + "/" + VOTER_ID))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String expected = jsonUtils.toJson(new UserTo(VOTER));
        assertJson(expected, actual);
    }

}