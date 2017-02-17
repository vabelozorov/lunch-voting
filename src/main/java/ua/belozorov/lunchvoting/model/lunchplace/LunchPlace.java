package ua.belozorov.lunchvoting.model.lunchplace;

import com.google.common.collect.ImmutableSortedSet;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.jetbrains.annotations.Nullable;
import ua.belozorov.lunchvoting.LengthEach;
import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;
import ua.belozorov.lunchvoting.util.hibernate.PhonesToStringConverter;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.util.*;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 15.11.16.
 */
@Entity
@Table(name = "places")
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
    @Convert(converter = PhonesToStringConverter.class)
    @SuppressWarnings("JpaAttributeTypeInspection")
    private final Set<String> phones;

    @OneToMany(mappedBy = "lunchPlace")
    private final Set<Menu> menus;

    protected LunchPlace() {
        super(null, null);
        this.phones = Collections.emptySet();
        this.menus = Collections.emptySet();
        this.description = null;
        this.address = null;
        this.name = null;
    }

    public LunchPlace(String name) {
        this.name = name;
        this.address = null;
        this.description = null;
        this.phones = new HashSet<>();
        this.menus = new HashSet<>();
    }

    public LunchPlace(@Nullable String id, String name, String address, String description,
                      Set<String> phones) {
        this(id, null, name, address, description, phones, Collections.emptySet());
    }

    @Builder(toBuilder = true)
    LunchPlace(@Nullable String id, @Nullable Integer version, String name, String address, String description,
                      Set<String> phones, Set<Menu> menus) {
        super(id, version);
        this.name = name;
        this.address = address;
        this.description = description;
        this.phones = new HashSet<>(phones);
        this.menus = new LinkedHashSet<>(menus);
    }

    public Set<String> getPhones() {
        return ImmutableSortedSet.copyOf(this.phones);
    }

    public Set<Menu> getMenus() {
        return ImmutableSortedSet.copyOf(this.menus);
    }

    public LunchPlace setMenus(Set<Menu> menus) {
        return this.toBuilder().menus(menus).build();
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

    @Override
    public int compareTo(LunchPlace o) {
        return this.name.compareTo(o.name);
    }
}

