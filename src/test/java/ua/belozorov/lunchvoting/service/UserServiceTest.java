package ua.belozorov.lunchvoting.service;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.belozorov.lunchvoting.ModelMatcher;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.UserRole;

import java.util.*;

import static org.junit.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
import static ua.belozorov.lunchvoting.UserTestData.*;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 17.11.16.
 */
public class UserServiceTest extends AbstractServiceTest {
    private static final Comparator<User> USER_COMPARATOR = new UserComparator();

    @Autowired
    private IUserService userService;

    @Test
    public void get() throws Exception {
        User actual = userService.get(VOTER_ID);
        assertThat(actual, userMatch(VOTER));
    }

    @Test
    public void getAll() throws Exception {
        Collection<User> users = userService.getAll();
        assertReflectionEquals(USERS, users.toArray(new User[users.size()])
        );
    }

    @Test
    public void delete() throws Exception {
        userService.delete(ADMIN_ID);
        Collection<User> users = userService.getAll();
        assertReflectionEquals(
            Arrays.asList(VOTER, TSAR),
            users.toArray(new User[users.size()])
        );
    }

    @Test
    public void update() throws Exception {
        User updated = userService.get(VOTER_ID);
        updated.setPassword("newPassword");
        updated.setEmail("updated@email.com");
        userService.update(updated);
        assertThat(userService.get(VOTER_ID), userMatch(updated));
    }

    @Test
    public void create() throws Exception {
        User expectedUser = new User("New User", "new@email.com", "strongPassword");
        expectedUser.setId("TestUser4");
        expectedUser.setRoles((byte) 3);
        User created = userService.create(expectedUser);
        expectedUser.setId(created.getId());
        assertThat(created, userMatch(expectedUser));
    }

    @Test
    public void activate() throws Exception {
        userService.activate(VOTER_ID, false);
        assertFalse(userService.get(VOTER_ID).isActivated());
    }

    @Test
    public void setRoles() throws Exception {
        userService.setRoles(VOTER_ID, (byte) 3);
        Set<UserRole> expectedRoles = new HashSet<>();
        expectedRoles.add(UserRole.VOTER);
        expectedRoles.add(UserRole.ADMIN);
        assertEquals(UserRole.toUserRoles(userService.get(VOTER_ID).getRoles()), expectedRoles);
    }

    @Test(expected = NotFoundException.class)
    public void getNotExisting() {
        userService.get("NotExistingId");
    }

    @Test(expected = NotFoundException.class)
    public void deleteNotExisting() {
        userService.delete("NotExistingId");
    }

    private static Matcher<User> userMatch(User user) {
        return new ModelMatcher<>(USER_COMPARATOR, user);
    }
}