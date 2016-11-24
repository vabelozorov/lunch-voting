package ua.belozorov.lunchvoting;

import ua.belozorov.lunchvoting.model.User;

import java.time.LocalDateTime;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 22.11.16.
 */
public class AuthorizedUser {
    public static String voterId() {
        return "TestUser1";
    }
    public static String adminId() {
        return "TestUser3";
    }

//    public static User voter() {
//        return new User("VOTER_ID", "Синий Гном", "voter@email.com", "voterpass", (byte) 1,
//                LocalDateTime.of(2016, 11, 17, 13, 0, 0), true);
//    }
//    public static User admin() {
//        return new User("ADMIN_ID", "Just an admin", "admin@email.com", "adminpass", (byte) 2,
//                LocalDateTime.of(2016, 11, 16, 13, 0, 0), true);
//    }
}
