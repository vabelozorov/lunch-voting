package ua.belozorov.lunchvoting.model.lunchplace;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSortedSet;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.*;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.belozorov.lunchvoting.LengthEach;
import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;
import ua.belozorov.lunchvoting.util.SetToStringConverter;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
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
@DynamicUpdate
public final class LunchPlace extends AbstractPersistableObject implements Comparable<LunchPlace> {

    @Column(name = "name", nullable = false)
    @NotBlank
    @Length(max = 50)
    private final String name;

    @Column(name = "address")
    @Length(max = 100)
    private final String address;

    @Column(name = "description")
    @Length(max = 1000)
    private final String description;

    @Column(name = "phones")
    @LengthEach//TODO
    @OrderBy
    @Convert(converter = SetToStringConverter.class)
    @SuppressWarnings("JpaAttributeTypeInspection")
    @Getter(AccessLevel.NONE)
    private final Set<String> phones;

    @OneToMany(mappedBy = "lunchPlace")
    private final Set<Menu> menus;

    @Column(name = "user_id")
    private final String adminId;

    protected LunchPlace() {
        super(null, null);
        this.phones = Collections.emptySet();
        this.menus = Collections.emptySet();
        this.adminId = null;
        this.description = null;
        this.address = null;
        this.name = null;
    }

    public LunchPlace(String id, String name, String address, String description,
                      Collection<String> phones, Collection<Menu> menus, String adminId) {
        this(id, null, name, address, description, phones, menus, adminId);
    }

    @Builder(toBuilder = true)
    LunchPlace(@Nullable String id, @Nullable Integer version, @NotNull String name, @NotNull String address, @NotNull String description,
                      @NotNull Collection<String> phones, @NotNull Collection<Menu> menus, @NotNull String adminId) {
        super(id, version);
        this.name = name;
        this.address = address;
        this.description = description;
        this.phones = new HashSet<>(phones);
        this.menus = new LinkedHashSet<>(menus);
        this.adminId = adminId;
//        this.name = Objects.requireNonNull(name, "name must not be null");
//        this.address = Objects.requireNonNull(address, "address must not be null");
//        this.description = Objects.requireNonNull(description, "description must not be null");
//        this.phones = phones == null ? new HashSet<>() : new HashSet<>(phones);
//        this.menus = menus == null ? new LinkedHashSet<>() : new LinkedHashSet<>(menus);
//        this.adminId = Objects.requireNonNull(adminId, "adminId must not be null");
    }

    public @NotNull Set<String> getPhones() {
        return ImmutableSortedSet.<String>naturalOrder().addAll(phones).build();
    }

    public LunchPlace setMenus(Collection<Menu> menus) {
//        return builder(this).menus(menus).build();
        return this.toBuilder().menus(menus).build();
    }

    @Override
    public String toString() {
        return "LunchPlace{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", description='" + description + '\'' +
                ", phones=" + phones +
                ", adminId='" + adminId + '\'' +
                '}';
    }

    public static LunchPlaceBuilder builder() {
        return new LunchPlaceBuilder();
    }

//    public static LunchPlaceBuilder builder(LunchPlace place) {
//        return new LunchPlaceBuilder(place);
//    }

    public Menu createMenu(LocalDate effectiveDate, List<Dish> dishes) {
        return new Menu(effectiveDate, dishes, this);
    }

    @Override
    public int compareTo(LunchPlace o) {
        int r = this.name.compareTo(o.name);
        return r != 0 ? r : this.id.compareTo(o.id);
    }

    public static class LunchPlaceBuilder {
        private String id, name, address, description;
        private Collection<String> phones = Collections.emptySet();
        private Collection<Menu> menus = Collections.emptySet();
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

