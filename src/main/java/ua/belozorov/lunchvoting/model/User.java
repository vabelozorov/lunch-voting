package ua.belozorov.lunchvoting.model;

import com.google.common.collect.Sets;
import lombok.Getter;
import org.hibernate.annotations.DynamicUpdate;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ua.belozorov.lunchvoting.model.area.EatingArea;
import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;
import ua.belozorov.lunchvoting.util.ExceptionUtils;
import ua.belozorov.lunchvoting.util.hibernate.RolesToIntegerConverter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ua.belozorov.lunchvoting.util.ExceptionUtils.NOT_CHECK;

/**
 * An immutable class that combines roles of a voter and a security principal.
 * <p>This class implements {@link Comparable} to support a natural sorting which is by its {@code email}.</p>
 * Created on 15.11.16.
 */
@Getter
@Entity
@Table(name = "users")
@DynamicUpdate
public final class User extends AbstractPersistableObject implements Comparable<User>, UserDetails {

    @Column(name = "name", nullable = false)
    private final String name;

    @Column(name = "email", nullable = false)
    private final String email;

    @Column(name = "password", nullable = false)
    private final String password;

    @Column(name = "roles", nullable = false)
    @Convert(converter = RolesToIntegerConverter.class)
    private final Set<UserRole> roles;

    @Column(name = "registered_date", nullable = false, updatable = false)
    private final LocalDateTime registeredDate;

    @Column(name = "activated", nullable = false)
    private final boolean activated;

    @Column(name = "area_id")
    @Getter(onMethod = @__({@Nullable}))
    private final String areaId;

    /**
     * JPA
     */
    User() {
        this.name = null;
        this.email = null;
        this.password = null;
        this.roles = new HashSet<>();
        this.registeredDate = null;
        this.activated = false;
        this.areaId = null;
    }

    /**
     * Simple constructor. All values must be non-null.
     * ID is auto-generated
     * Time of user registration is set to a current time, user is active and
     * has a role of <code>VOTER</code>. User does not belong to any area.
     *
     * @param name username
     * @param email email, must be unique
     * @param password password
     */
    public User(String name, String email, String password) {
        this(null, null, name, email, password, Sets.newHashSet(UserRole.VOTER),
                LocalDateTime.now(), true, null);
    }

    /**
     * All-args public constructor.
     * <code>id, areaId</code> can be null.
     * @param id any string or null to auto-generate
     * @param name
     * @param email, must be unique
     * @param password
     * @param roles
     * @param registeredDate
     * @param activated
     * @param areaId ID of an existing {@link EatingArea} or null
     */
    public User(@Nullable String id, String name, String email, String password, Set<UserRole> roles,
                LocalDateTime registeredDate, boolean activated, @Nullable String areaId) {
        this(id, null, name, email, password,roles, registeredDate, activated, areaId);
    }

    /**
     * All-args constructor for cloning setters
     * @param id any string or null to auto-generate
     * @param version a positive value to indicate a persisted instance or null for a transient instance
     * @param name
     * @param email, must be unique
     * @param password
     * @param roles
     * @param registeredDate
     * @param activated
     * @param areaId ID of an existing {@link EatingArea} or null
     */
    private User(@Nullable String id, @Nullable Integer version, String name, String email, String password, Set<UserRole> roles,
                LocalDateTime registeredDate, boolean activated, @Nullable String areaId) {
        super(id, version);

        ExceptionUtils.checkParamsNotNull(NOT_CHECK, NOT_CHECK, name, email, password, roles, registeredDate, activated);

        this.name = name;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.registeredDate = registeredDate;
        this.activated = activated;
        this.areaId = areaId;
    }

    public Set<UserRole> getRoles() {
        return Collections.unmodifiableSet(this.roles);
    }

    public User withName(String name) {
        return new User(id, version, name, email, password, roles, registeredDate, activated, areaId);
    }

    public User withEmail(String email) {
        return new User(id, version, name, email, password, roles, registeredDate, activated, areaId);
    }

    public User withPassword(String password) {
        return new User(id, version, name, email, password, roles, registeredDate, activated, areaId);
    }

    public User withRoles(Set<UserRole> roles) {
        return new User(id, version, name, email, password, Sets.newHashSet(roles), registeredDate, activated, areaId);
    }

    public User addRole(UserRole role) {
        Set<UserRole> roles = new HashSet<>(this.getRoles());
        roles.add(role);
        return new User(id, version, name, email, password, roles, registeredDate, activated, areaId);
    }

    public User withActivated(boolean activated) {
        return new User(id, version, name, email, password, roles, registeredDate, activated, areaId);
    }

    public User assignAreaId(String areaId) {
        return new User(id, version, name, email, password, roles, registeredDate, activated, areaId);
    }

    public boolean belongsToArea() {
        return this.areaId != null;
    }

    @Override
    public int compareTo(User o) {
        return this.email.compareTo(o.email);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", roles=" + roles +
                ", registeredDate=" + registeredDate +
                ", activated=" + activated +
                ", areaId=" + areaId +
                '}';
    }

    /*
            UserDetails methods for Spring Security
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.getRoles().stream().map(m -> (GrantedAuthority)m).collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isActivated();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
