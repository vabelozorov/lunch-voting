package ua.belozorov.lunchvoting.to;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.SafeHtml;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.UserRole;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;

/**
 *
 * Created on 15.11.16.
 */
@Getter
@Setter
public final class UserTo {

    @SafeHtml
    private String id;

    @NotBlank(groups = {Create.class, Update.class})
    @SafeHtml
    @Size(min = 2, max = 100)
    private String name;

    @Email(groups = {Create.class, Update.class})
    @NotBlank(groups = {Create.class, Update.class})
    private String email;

    @Size(min = 6, max = 30, groups = {Create.class, Update.class})
    private String password;

    private Set<UserRole> roles;

    private LocalDateTime registeredDate;

    @Getter(AccessLevel.NONE)
    private Boolean activated;

    private String areaId;

    protected UserTo() {
    }

    public UserTo(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public UserTo(String id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public UserTo(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.roles = user.getRoles();
        this.registeredDate = user.getRegisteredDate();
        this.activated = user.isActivated();
        this.areaId = user.getAreaId();
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

    public boolean isActivated() {
        return activated;
    }

    /* Marker interfaces for JSR-303 group validation
     */
    public interface Create {}

    public interface Update {}
}
