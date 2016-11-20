package ua.belozorov.lunchvoting.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * <h2>Enum that describes user roles</h2>
 * <p/>
 * {@code id} field is used to support the representation of a user roles set as a bitmask
 *
 * @author vabelozorov on 15.11.16.
 */
public enum UserRole {
    VOTER((byte)1),
    ADMIN((byte)2);

    private final byte id;

    UserRole(byte id) {
        this.id = id;
    }

    public byte id() {
        return id;
    }

    public static Set<UserRole> toUserRoles(byte bitmask) {
        Set<UserRole> roles = new HashSet<>();
        if (bitmask == 0) {
            return roles;
        }

        if ((bitmask & (1<<0)) == UserRole.VOTER.id()) {
            roles.add(UserRole.VOTER);
        }
        if ((bitmask & (1<<1)) == UserRole.ADMIN.id()) {
            roles.add(UserRole.ADMIN);
        }
        return roles;
    }

    public static byte rolesToBitmask(Collection<UserRole> roles) {
        byte bitmask = 0;
        for (UserRole role : roles) {
            bitmask += role.id();
        }
        return bitmask;
    }
}
