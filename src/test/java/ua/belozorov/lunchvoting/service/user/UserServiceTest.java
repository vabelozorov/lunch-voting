package ua.belozorov.lunchvoting.service.user;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.UserRole;
import ua.belozorov.lunchvoting.service.AbstractServiceTest;
import ua.belozorov.lunchvoting.service.user.UserService;

import java.util.*;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;
import static ua.belozorov.lunchvoting.MatcherUtils.matchCollection;
import static ua.belozorov.lunchvoting.MatcherUtils.matchSingle;
import static ua.belozorov.lunchvoting.testdata.UserTestData.*;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 17.11.16.
 */
public class UserServiceTest extends AbstractServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void get() throws Exception {
        User actual = userService.get(VOTER_ID);
        assertThat(actual, matchSingle(VOTER, USER_COMPARATOR));
    }

    @Test
    public void getAll() throws Exception {
        Collection<User> users = userService.getAll();
        assertThat(
                Arrays.asList(ADMIN, GOD, VOTER),
                contains(matchCollection(users, USER_COMPARATOR))
        );
    }

    @Test
    public void delete() throws Exception {
        userService.delete(ADMIN_ID);
        Collection<User> users = userService.getAll();
        assertThat(
            Arrays.asList(GOD, VOTER),
            contains(matchCollection(users, USER_COMPARATOR))
        );
    }

    @Test
    public void update() throws Exception {
        User updated = userService.get(VOTER_ID);
        updated = User.builder(updated).password("newPassword").email("updated@email.com").build();
        userService.update(updated);
        assertThat(userService.get(VOTER_ID), matchSingle(updated, USER_COMPARATOR));
    }

    @Test
    public void create() throws Exception {
        User expected = new User("NEW_USER_ID", "New User", "new@email.com", "strongPassword");
        User actual = userService.create(expected);
        expected = User.builder(expected)
                .registeredDate(actual.getRegisteredDate())
                .activated(true)
                .roles(UserRole.VOTER.id()).build();
        assertThat(actual, matchSingle(expected, USER_COMPARATOR));
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

    @Test //TODO
    public void updateNotExisting() {

    }

    @Test(expected = NotFoundException.class)
    public void deleteNotExisting() {
        userService.delete("NotExistingId");
    }

    @Test //TODO
    public void createDuplicate() {

    }

}