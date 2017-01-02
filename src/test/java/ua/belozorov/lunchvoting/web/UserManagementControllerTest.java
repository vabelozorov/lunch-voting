package ua.belozorov.lunchvoting.web;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import ua.belozorov.lunchvoting.JsonUtils;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.UserRole;
import ua.belozorov.lunchvoting.service.user.UserService;
import ua.belozorov.lunchvoting.to.UserTo;
import ua.belozorov.lunchvoting.to.transformers.UserTransformer;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
import static ua.belozorov.lunchvoting.MatcherUtils.*;
import static ua.belozorov.lunchvoting.testdata.UserTestData.*;


/**
 * <h2></h2>
 *
 * @author vabelozorov on 17.11.16.
 */

public class UserManagementControllerTest extends AbstractControllerTest {
    private static final String REST_URL = UserManagementController.REST_URL;

    @Autowired
    private UserService userService;

    @Test
    public void testCreate() throws Exception {
        UserTo userTo = new UserTo(null, "New User", "new@email.com", "strongPassword", UserRole.VOTER.id(), null, true);

        MvcResult result = mockMvc
                .perform(post(REST_URL)
                        .content(jsonUtils.toJson(userTo))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        UserTo created = jsonUtils.fromMvcResultBody(result, UserTo.class);
        userTo.setId(created.getId());
        userTo.setRegisteredDate(created.getRegisteredDate());

        assertThat(created, matchByToString(userTo));
    }

    @Test
    public void testUpdate() throws Exception {
        UserTo userTo = UserTransformer.toDto(VOTER);
        userTo.setPassword("newPassword");
        userTo.setEmail("newEmail@email.com");

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
        MvcResult result = mockMvc.perform(get(REST_URL + "/" + VOTER_ID))
                .andExpect(status().isOk())
                .andReturn();

        UserTo actual = jsonUtils.fromMvcResultBody(result, UserTo.class);
        UserTo expected = UserTransformer.toDto(VOTER);
        assertThat(actual, matchByToString(expected));
    }

    @Test
    public void testGetAll() throws Exception {
        MvcResult result = mockMvc.perform(get(REST_URL))
                .andExpect(status().isOk())
                .andReturn();

        Collection<UserTo> actual = jsonUtils.fromMvcResultBody(result, new TypeReference<Collection<UserTo>>() {});
        List<UserTo> expected = USERS.stream()
                .map(UserTransformer::toDto)
                .sorted(Comparator.comparing(UserTo::getEmail))
                .collect(Collectors.toList());

        assertReflectionEquals(expected, actual);
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete(REST_URL + "/" + VOTER_ID))
                .andExpect(status().isNoContent());
        Collection<User> actual = userService.getAll();
        assertThat(
                actual,
                contains(matchCollection(Arrays.asList(ADMIN, GOD), USER_COMPARATOR))
        );
    }

    @Test
    public void activate() throws Exception {
        mockMvc.perform(put(REST_URL + "/" + VOTER_ID + "/activate")
                            .content(jsonUtils.toJson("isActive", "false"))
                            .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        User user = userService.get(VOTER_ID);
        assertFalse(user.isActivated());
    }

    @Test
    public void setRights() throws Exception {
        mockMvc.perform(put(REST_URL + "/" + VOTER_ID + "/rights.set")
                .content(jsonUtils.toJson("rights", "3"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        User user = userService.get(VOTER_ID);
        Set<UserRole> expectedRoles = new HashSet<>();
        expectedRoles.add(UserRole.VOTER);
        expectedRoles.add(UserRole.ADMIN);
        assertEquals(UserRole.toUserRoles(user.getRoles()), expectedRoles);
    }
}