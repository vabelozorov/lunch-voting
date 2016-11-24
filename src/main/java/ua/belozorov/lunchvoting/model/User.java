package ua.belozorov.lunchvoting.model;

import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 15.11.16.
 */
@Entity
@Table(name = "users")
@Getter
@Immutable
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
    private final byte roles;

    @Column(name = "registeredDate", nullable = false, updatable = false)
    private final LocalDateTime registeredDate;

    @Column(name = "activated", nullable = false)
    private final boolean activated;

    /**
     * Primarily for Hibernate. Constructs a User instance with default values. Providing {@code null} as an ID
     * will result in the object retaining its auto-generated ID.
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
        this(id, name, email, password,  UserRole.VOTER.id(), LocalDateTime.now(), true);
    }

    /**
     * A constructor that accepts all available parameters except version
     *
     * @param name
     * @param email
     * @param password
     * @param roles
     * @param registeredDate
     * @param activated
     */
    @Builder
    public User(String id, String name, String email, String password, byte roles,
                LocalDateTime registeredDate, boolean activated) {
        super(id);
        this.name = name;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.registeredDate = registeredDate;
        this.activated = activated;
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

    public static class UserBuilder {
        private String id, name, email, password;
        private byte roles;
        private LocalDateTime registeredDate;
        private boolean activated;

        UserBuilder() { }

        UserBuilder(User user) {
            this.id = user.id;
            this.name = user.name;
            this.email = user.email;
            this.password = user.password;
            this.roles = user.roles;
            this.registeredDate = user.registeredDate;
            this.activated = user.activated;
        }
    }
}
