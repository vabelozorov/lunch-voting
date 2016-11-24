package ua.belozorov.lunchvoting.testdata;

import ua.belozorov.lunchvoting.model.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 17.11.16.
 */
public class UserTestData {
    public static final Comparator<User> USER_COMPARATOR = new UserComparator();

    public static User GOD;
    public static User VOTER;
    public static User ADMIN;
    public static String GOD_ID;
    public static String VOTER_ID;
    public static String ADMIN_ID;
    public static Collection<User> USERS;

    static {
        GOD = new User("GOD_ID", "Царь всея приложение", "god@email.com", "godpass", (byte)3,
                LocalDateTime.of(2016, 11, 16, 0, 0, 1), true);
        GOD_ID = GOD.getId();

        VOTER = new User("VOTER_ID", "Синий Гном", "voter@email.com", "voterpass", (byte) 1,
                LocalDateTime.of(2016, 11, 17, 13, 0, 0), true);
        VOTER_ID = VOTER.getId();

        ADMIN = new User("ADMIN_ID", "Just an admin", "admin@email.com", "adminpass", (byte) 2,
                LocalDateTime.of(2016, 11, 16, 13, 0, 0), true);
        ADMIN_ID = ADMIN.getId();

        USERS = Arrays.asList(ADMIN, VOTER, GOD);
    }

    public static class UserComparator implements Comparator<User>{
        @Override
        public int compare(User o1, User o2) {
            return (o1.getId().equals(o2.getId()) &&
                    o1.getName().equals(o2.getName()) &&
                    o1.getEmail().equals(o2.getEmail()) &&
                    o1.getPassword().equals(o2.getPassword()) &&
                    o1.getRegisteredDate().isEqual(o2.getRegisteredDate()) &&
                    o1.getRoles() == o2.getRoles() &&
                    o1.isActivated() == o2.isActivated() ) ? 0 : -1;
        }
    }
}
