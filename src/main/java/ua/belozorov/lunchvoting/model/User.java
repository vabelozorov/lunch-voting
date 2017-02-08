package ua.belozorov.lunchvoting.model;

import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;
import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;
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
public class User extends AbstractPersistableObject {

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

    /**
     * Primarily for Hibernate. Constructs a User instance with default values. Providing {@code null} as an ID
     * will getByPollItem in the object retaining its auto-generated ID.
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
        this(id, null, name, email, password,  new HashSet<>(Collections.singletonList(UserRole.VOTER)), LocalDateTime.now(), true);
    }

    @Builder
    public User(String id, String name, String email, String password, Set<UserRole> roles,
                LocalDateTime registeredDate, boolean activated) {
        this(id, null, name, email, password,roles, registeredDate, activated);
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
    public User(String id, Integer version, String name, String email, String password, Set<UserRole> roles,
                LocalDateTime registeredDate, boolean activated) {
        super(id, version);
        this.name = name;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.registeredDate = registeredDate;
        this.activated = activated;
    }

    public User setActivated(boolean activated) {
        return builder(this).activated(activated).build();
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
                '}';
    }

    public static UserBuilder builder(User user) {
        return new UserBuilder(user);
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public User setRoles(Set<UserRole> roles) {
        return User.builder(this).roles(roles).build();
    }

    public static class UserBuilder {
        private String id, name, email, password;
        private Integer version;
        private Set<UserRole> roles;
        private LocalDateTime registeredDate;
        private boolean activated;

        UserBuilder() { }

        UserBuilder(User user) {
            this.id = user.id;
            this.version = user.version;
            this.name = user.name;
            this.email = user.email;
            this.password = user.password;
            this.roles = user.roles;
            this.registeredDate = user.registeredDate;
            this.activated = user.activated;
        }

        public User build() {
            return new User(this.id, this.version, this.name, this.email, this.password, this.roles, this.registeredDate, this.activated);
        }
    }
}
