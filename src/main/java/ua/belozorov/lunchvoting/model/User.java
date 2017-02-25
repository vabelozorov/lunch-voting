package ua.belozorov.lunchvoting.model;

import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;
import ua.belozorov.lunchvoting.util.hibernate.RolesToIntegerConverter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <h2></h2>
 *
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
    private final String areaId;

    /**
     * Primarily for Hibernate. Constructs a User instance with default values.
     */
    protected User() {
        this( null, null, null);
    }

    /**
     * For a convenient creation from a DTO.
     *
     * @param name
     * @param email
     * @param password
     */
    public User(String name, String email, String password) {
        this(null, null, name, email, password,  new HashSet<>(Collections.singletonList(UserRole.VOTER)),
                LocalDateTime.now(), true, null);
    }

    public User(String id, String name, String email, String password, Set<UserRole> roles,
                LocalDateTime registeredDate, boolean activated) {
        this(id, null, name, email, password,roles, registeredDate, activated, null);
    }
    /**
     * A constructor that accepts all available parameters
     *
     * @param name
     * @param email
     * @param password
     * @param roles
     * @param registeredDate
     * @param activated
     */
    @Builder(toBuilder = true)
    User(String id, Integer version, String name, String email, String password, Set<UserRole> roles,
                LocalDateTime registeredDate, boolean activated, String areaId) {
        super(id, version);
        this.name = name;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.registeredDate = registeredDate;
        this.activated = activated;
        this.areaId = areaId;
    }

    public User setActivated(boolean activated) {
        return this.toBuilder().activated(activated).build();
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

    public User setRoles(Set<UserRole> roles) {
        return this.toBuilder().roles(roles).build();
    }

    public User addRole(UserRole role) {
        Set<UserRole> roles = new HashSet<>(this.getRoles());
        roles.add(role);
        return this.toBuilder().roles(roles).build();
    }

    public User assignAreaId(String id) {
        return this.toBuilder().areaId(id).build();
    }

    public boolean belongsToArea() {
        return this.areaId != null;
    }

    @Override
    public int compareTo(User o) {
        return this.email.compareTo(o.email);
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
