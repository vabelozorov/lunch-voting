package ua.belozorov.lunchvoting;

import ua.belozorov.lunchvoting.model.User;

import java.time.LocalDateTime;
import java.util.Comparator;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 17.11.16.
 */
public class UserTestData {
    public static User TSAR;
    public static User VOTER;
    public static User ADMIN;
    public static String TSAR_ID;
    public static String VOTER_ID;
    public static String ADMIN_ID;

    static {
        TSAR = new User(0, "Царь всея приложение", "tsar@email.com", "tsarpass", (byte)3,
                LocalDateTime.of(2016, 11, 16, 0, 0, 1), true);
        TSAR.setId("TestUser2");
        TSAR_ID = TSAR.getId();

        VOTER = new User(0, "Синий Гном", "gnom@email.com", "gnompass", (byte) 1,
                LocalDateTime.of(2016, 11, 17, 13, 0, 0), true);
        VOTER.setId("TestUser1");
        VOTER_ID = VOTER.getId();

        ADMIN = new User(0, "Just an admin", "admin@email.com", "godpass", (byte) 2,
                LocalDateTime.of(2016, 11, 16, 13, 0, 0), true);
        ADMIN.setId("TestUser3");
        ADMIN_ID = ADMIN.getId();
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
