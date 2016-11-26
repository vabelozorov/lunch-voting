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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

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
    @Fetch(FetchMode.JOIN)
    private final Collection<String> phones;

    @OneToMany(mappedBy = "lunchPlace")
    private final Collection<Menu> menus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User admin;

    protected LunchPlace() {
        this(null, null, null, null, null, Collections.emptyList(), Collections.emptyList(), null);
    }

    @Builder
    public LunchPlace(String id, String name, String address, String description,
                      Collection<String> phones, Collection<Menu> menus) {
        this(id, null, name, address, description, phones, menus, null);
    }

    public LunchPlace(String id, Integer version, String name, String address, String description,
                      Collection<String> phones, Collection<Menu> menus, User admin) {
        super(id, version);
        this.name = name;
        this.address = address;
        this.description = description;
        this.phones = Objects.requireNonNull(phones);
        this.menus = Objects.requireNonNull(menus);
        this.admin = admin;
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

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public static LunchPlaceBuilder builder() {
        return new LunchPlaceBuilder();
    }

    public static LunchPlaceBuilder builder(LunchPlace place) {
        return place == null ? new LunchPlaceBuilder() : new LunchPlaceBuilder(place);
    }

    public static class LunchPlaceBuilder {
        private String id, name, address, description;
        private Collection<String> phones;
        private Collection<Menu> menus;
        private User admin;
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
            this.admin = place.admin;
        }

        public LunchPlace build() {
            return new LunchPlace(this.id, this.version, this.name, this.address, this.description, this.phones, this.menus, this.admin);
        }
    }
}

