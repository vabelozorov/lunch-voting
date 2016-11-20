package ua.belozorov.lunchvoting.model;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 15.11.16.
 */
@Entity
@Table(name = "users")
public class User extends AbstractPersistableObject {

    @Column(name = "name", nullable = false)
    @NotEmpty
    private String name;

    @Column(name = "email", nullable = false)
    @Email
    private String email;

    @Column(name = "password", nullable = false)
    @Length(min = 6)
    private String password;

    @Column(name = "roles", nullable = false)
    private byte roles = UserRole.VOTER.id();

    @Column(name = "registeredDate", nullable = false)
    private LocalDateTime registeredDate = LocalDateTime.now();

    @Column(name = "activated", nullable = false)
    private boolean activated = true;

    public User() {
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    /**
     * Intended use - to create a test User which can be fully initialized by invoking a contructor
     *
     * @param name
     * @param email
     * @param password
     * @param roles
     * @param registeredDate
     * @param activated
     */
    public User(String name, String email, String password, byte roles,
                LocalDateTime registeredDate, boolean activated) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.registeredDate = registeredDate;
        this.activated = activated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte getRoles() {
        return roles;
    }

    public void setRoles(byte roles) {
        this.roles = roles;
    }

    public LocalDateTime getRegisteredDate() {
        return registeredDate;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", roles=" + roles +
                ", registeredDate=" + registeredDate +
                ", activated=" + activated +
                '}';
    }
}
