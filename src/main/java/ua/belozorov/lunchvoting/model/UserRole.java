package ua.belozorov.lunchvoting.model;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * <h2>Enum that describes user roles</h2>
 * <p/>
 * {@code id} field is used to support the representation of a user roles set as a bitmask
 *
 * Created on 15.11.16.
 */
public enum UserRole implements GrantedAuthority {
    VOTER((byte)1),
    ADMIN((byte)2),
    CREATOR((byte)4);

    private final byte id;

    UserRole(byte id) {
        this.id = id;
    }

    public byte id() {
        return id;
    }

    @Override
    public String getAuthority() {
        return "ROLE_" + this.name();
    }

    public static Set<UserRole> toUserRoles(Integer bitmask) {
        Set<UserRole> roles = new HashSet<>();
        if (bitmask == 0) {
            return roles;
        }

        if ((bitmask & (1<<0)) != 0) {
            roles.add(UserRole.VOTER);
        }
        if ((bitmask & (1<<1)) != 0) {
            roles.add(UserRole.ADMIN);
        }
        return roles;
    }

    public static Integer rolesToBitmask(Collection<UserRole> roles) {
        Integer bitmask = 0;
        for (UserRole role : roles) {
            bitmask += role.id();
        }
        return bitmask;
    }
}
