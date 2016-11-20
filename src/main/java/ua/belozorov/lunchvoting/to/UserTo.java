package ua.belozorov.lunchvoting.to;

import ua.belozorov.lunchvoting.model.UserRole;

import java.time.LocalDateTime;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 15.11.16.
 */
public class UserTo {
    private String id;
    private String name;
    private String email;
    private String password;
    private byte roles = UserRole.VOTER.id();
    private LocalDateTime registeredDate;
    private boolean activated;

    public UserTo() {
    }

    public UserTo(String id, String name, String email, String password, byte roles, LocalDateTime registeredDate, boolean activated) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.registeredDate = registeredDate;
        this.activated = activated;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public byte getRoles() {
        return roles;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRoles(byte roles) {
        this.roles = roles;
    }

    public LocalDateTime getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(LocalDateTime registeredDate) {
        this.registeredDate = registeredDate;
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
        return "UserTo{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", roles=" + roles +
                ", registeredDate=" + registeredDate +
                ", activated=" + activated +
                '}';
    }
}
