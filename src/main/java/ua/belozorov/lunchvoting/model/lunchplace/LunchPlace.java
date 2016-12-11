package ua.belozorov.lunchvoting.model.lunchplace;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import ua.belozorov.lunchvoting.LengthEach;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 15.11.16.
 */
@Entity
@Table(name = "places",
        uniqueConstraints = @UniqueConstraint(name = "name_user_id_unique", columnNames = {"name", "user_id"})
)
@Getter
public class LunchPlace extends AbstractPersistableObject {

    @Column(name = "name", nullable = false)
    @NotEmpty
    @Length(max = 50)
    private final String name;

    @Column(name = "address")
    @Length(max = 100)
    private final String address;

    @Column(name = "description")
    @Length(max = 1000)
    private final String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "phones", joinColumns = @JoinColumn(name = "place_id"))
    @Column(name = "phone")
    @LengthEach
    @OrderBy
    private final Collection<String> phones;

    @OneToMany(mappedBy = "lunchPlace", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private final Collection<Menu> menus;

    @Column(name = "user_id")
    private final String adminId;

    protected LunchPlace() {
        super(null, null);
        this.phones = null;
        this.menus = null;
        this.adminId = null;
        this.description = null;
        this.address = null;
        this.name = null;
    }

    public LunchPlace(String id, String name, String address, String description,
                      Collection<String> phones, Collection<Menu> menus, String adminId) {
        this(id, null, name, address, description, phones, menus, adminId);
    }

    @Builder
    public LunchPlace(String id, Integer version, String name, String address, String description,
                      Collection<String> phones, Collection<Menu> menus, String adminId) {
        super(id, version);
        this.name = name;
        this.address = address;
        this.description = description;
        this.phones = Objects.requireNonNull(phones);
        this.menus = Objects.requireNonNull(menus);
        this.adminId = adminId;
    }

    public LunchPlace setMenus(Collection<Menu> menus) {
        return builder(this).menus(menus).build();
    }

    @Override
    public String toString() {
        return "LunchPlace{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", description='" + description + '\'' +
                ", phones=" + phones +
                '}';
    }

    public static LunchPlaceBuilder builder() {
        return new LunchPlaceBuilder();
    }

    public static LunchPlaceBuilder builder(LunchPlace place) {
        return new LunchPlaceBuilder(place);
    }

    public Menu createMenu(LocalDate effectiveDate, List<Dish> dishes) {
        return new Menu(effectiveDate, dishes, this);
    }

    public static class LunchPlaceBuilder {
        private String id, name, address, description;
        private Collection<String> phones;
        private Collection<Menu> menus;
        private String adminId;
        private Integer version;

        LunchPlaceBuilder() { }

        LunchPlaceBuilder(LunchPlace place) {
            this.version = place.version;
            this.id = place.id;
            this.name = place.name;
            this.address = place.address;
            this.description = place.description;
            this.phones = place.phones;
            this.menus = place.menus;
            this.adminId = place.adminId;
        }

        public LunchPlace build() {
            return new LunchPlace(this.id, this.version, this.name, this.address, this.description, this.phones, this.menus, this.adminId);
        }
    }
}

