package ua.belozorov.lunchvoting.web;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import ua.belozorov.lunchvoting.TestUtils;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.UserRole;
import ua.belozorov.lunchvoting.service.IUserService;
import ua.belozorov.lunchvoting.to.UserTo;
import ua.belozorov.lunchvoting.util.UserUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
import static ua.belozorov.lunchvoting.UserTestData.*;


/**
 * <h2></h2>
 *
 * @author vabelozorov on 17.11.16.
 */

public class UserManagementControllerTest extends AbstractControllerTest {
    public static final String REST_URL = UserManagementController.REST_URL;

    @Autowired
    private IUserService userService;

    @Test
    public void testCreate() throws Exception {
        UserTo userTo = new UserTo(null, "New User", "new@email.com", "strongPassword", UserRole.VOTER.id(), null, true);

        MvcResult result = mockMvc
                .perform(post(REST_URL)
                        .content(TestUtils.toJson(userTo))
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
        UserTo userTo = UserUtils.convertIntoTo(VOTER);
        userTo.setPassword("newPassword");
        userTo.setEmail("newEmail@email.com");

        mockMvc.perform(put(REST_URL)
                        .content(TestUtils.toJson(userTo))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        User user = userService.get(VOTER_ID);

        assertEquals(userTo.getPassword(), user.getPassword());
        assertEquals(userTo.getEmail(), user.getEmail());
    }

    @Test
    public void testGet() throws Exception {
        MvcResult result = mockMvc.perform(get(REST_URL + "/" + VOTER_ID))
                .andExpect(status().isOk())
                .andReturn();

        UserTo actual = TestUtils.mvcResultToObject(result, UserTo.class);
        UserTo expected = UserUtils.convertIntoTo(VOTER);
        assertThat(actual, usertoMatch(expected));
    }

    @Test
    public void testGetAll() throws Exception {
        MvcResult result = mockMvc.perform(get(REST_URL))
                .andExpect(status().isOk())
                .andReturn();

        Collection<UserTo> actual = TestUtils.mvcResultToObject(result, new TypeReference<Collection<UserTo>>() {});
        List<UserTo> expected = USERS.stream().map(UserUtils::convertIntoTo).collect(Collectors.toList());

        assertReflectionEquals(expected, actual);
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete(REST_URL + "/" + VOTER_ID))
                .andExpect(status().isNoContent());
        Collection<User> actual = userService.getAll();
        assertReflectionEquals(Arrays.asList(ADMIN, TSAR), actual);
    }

    @Test
    public void activate() throws Exception {
        mockMvc.perform(put(REST_URL + "/" + VOTER_ID + "/activate")
                            .content(TestUtils.toJson("isActive", "false"))
                            .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        User user = userService.get(VOTER_ID);
        assertFalse(user.isActivated());
    }

    @Test
    public void setRights() throws Exception {
        mockMvc.perform(put(REST_URL + "/" + VOTER_ID + "/rights.set")
                .content(TestUtils.toJson("rights", "3"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        User user = userService.get(VOTER_ID);
        Set<UserRole> expectedRoles = new HashSet<>();
        expectedRoles.add(UserRole.VOTER);
        expectedRoles.add(UserRole.ADMIN);
        assertEquals(UserRole.toUserRoles(user.getRoles()), expectedRoles);
    }

}