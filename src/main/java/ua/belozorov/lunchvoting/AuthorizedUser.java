package ua.belozorov.lunchvoting;

import ua.belozorov.lunchvoting.model.User;

import java.time.LocalDateTime;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 22.11.16.
 */
public class AuthorizedUser {
    private static User GOD = GOD = new User("GOD_ID", 1, "Царь всея приложение", "god@email.com", "godpass", (byte)3,
            LocalDateTime.of(2016, 11, 16, 0, 0, 1), true);
    private static User VOTER = new User("VOTER_ID", 1, "Синий Гном", "voter@email.com", "voterpass", (byte) 1,
            LocalDateTime.of(2016, 11, 17, 13, 0, 0), true);
    private static User ADMIN = new User("ADMIN_ID", 1, "Just an admin", "admin@email.com", "adminpass", (byte) 2,
            LocalDateTime.of(2016, 11, 16, 13, 0, 0), true);

    private static User authorizedUser;
    static {
        setGod();
    }

    public static User get() {
        return authorizedUser;
    }

    public static void setVoter() {
        authorizedUser = VOTER;
    }

    public static void setAdmin() {
        authorizedUser = ADMIN;
    }

    public static void setGod() {
        authorizedUser = GOD;
    }
}
