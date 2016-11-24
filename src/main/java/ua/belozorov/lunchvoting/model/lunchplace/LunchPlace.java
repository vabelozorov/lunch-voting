package ua.belozorov.lunchvoting.model.lunchplace;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import ua.belozorov.lunchvoting.LengthEach;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;

import javax.persistence.*;
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
    private final Collection<String> phones;

    @OneToMany(mappedBy = "lunchPlace")
    @Singular
    private final Collection<Menu> menus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private final User admin;

    protected LunchPlace() {
        this(null, null, null, null, Collections.emptyList(), Collections.emptyList(), null);
    }

    @Builder
    public LunchPlace(String id, String name, String address, String description,
                      Collection<String> phones, Collection<Menu> menus, User admin) {
        super(id);
        this.name = name;
        this.address = address;
        this.description = description;
        this.phones = Objects.requireNonNull(phones);
        this.menus = menus;
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

    public static LunchPlaceBuilder builder() {
        return new LunchPlaceBuilder();
    }

    public static LunchPlaceBuilder builder(LunchPlace place) {
        return new LunchPlaceBuilder(place);
    }

    public static class LunchPlaceBuilder {
        private String id, name, address, description;
        private Collection<String> phones;
        private Collection<Menu> menus;
        private User admin;

        LunchPlaceBuilder() { }

        LunchPlaceBuilder(LunchPlace place) {
            this.id = place.id;
            this.name = place.name;
            this.address = place.address;
            this.description = place.description;
            this.phones = place.phones;
            this.menus = place.menus;
            this.admin = place.admin;
        }
    }
}

