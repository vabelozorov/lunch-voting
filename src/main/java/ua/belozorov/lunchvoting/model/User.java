package ua.belozorov.lunchvoting.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;
import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;
import ua.belozorov.lunchvoting.model.lunchplace.EatingArea;
import ua.belozorov.lunchvoting.util.RolesToIntegerConverter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 15.11.16.
 */
@Getter
@Entity
@Table(name = "users")
@DynamicUpdate
public final class User extends AbstractPersistableObject {

    @Column(name = "name", nullable = false)
    @NotEmpty
    private final String name;

    @Column(name = "email", nullable = false)
    @Email
    private final String email;

    @Column(name = "password", nullable = false)
    @Length(min = 6)
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
        this(null, null, null, null);
    }

    /**
     * For a convenient creation from a DTO.
     *
     * @param id
     * @param name
     * @param email
     * @param password
     */
    public User(String id, String name, String email, String password) {
        this(id, null, name, email, password,  new HashSet<>(Collections.singletonList(UserRole.VOTER)),
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
                ", password='" + password + '\'' +
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

}
