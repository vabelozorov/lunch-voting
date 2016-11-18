package ua.belozorov.lunchvoting.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import ua.belozorov.lunchvoting.TestUtils;
import ua.belozorov.lunchvoting.model.UserRole;
import ua.belozorov.lunchvoting.to.UserTo;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 17.11.16.
 */

public class UserManagementControllerTest extends AbstractControllerTest {
    public static final String REST_URL = UserManagementController.REST_URL;

    @Test
    public void testCreate() throws Exception {
        UserTo userTo = new UserTo(null, "New User", "new@email.com", UserRole.USER.id(), null, true);
        userTo.setPassword("strongPassword");
        String sentContent = TestUtils.toJson(userTo);

        MvcResult result = mockMvc
                .perform(post(REST_URL)
                        .content(sentContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        UserTo created = TestUtils.mvcResultToObject(result, UserTo.class);
        userTo.setId(created.getId());
        userTo.setRegisteredDate(created.getRegisteredDate());

        assertThat(created, usertoMatch(userTo));
    }

    @Test
    public void testUpdate() throws Exception {
//        UserTo userTo =
//        String sentContent = TestUtils.toJson(userTo, "password", "strongPassword");
//
//        MvcResult result = mockMvc
//                .perform(post(REST_URL)
//                        .content(sentContent)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isCreated())
//                .andReturn();
//
//        UserTo created = TestUtils.mvcResultToObject(result, UserTo.class);
//        userTo.setId(created.getId());
//        userTo.setRegisteredDate(created.getRegisteredDate());
//
//        assertThat(created, usertoMatch(userTo));
    }

    @Test
    public void testGet() throws Exception {

    }

    @Test
    public void getAll() throws Exception {

    }

    @Test
    public void delete() throws Exception {

    }

    @Test
    public void activate() throws Exception {

    }

    @Test
    public void setRights() throws Exception {

    }

}