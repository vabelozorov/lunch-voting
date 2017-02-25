package ua.belozorov.lunchvoting.model.lunchplace;

import com.google.common.collect.ImmutableSortedSet;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.*;
import org.jetbrains.annotations.Nullable;
import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;
import ua.belozorov.lunchvoting.util.ExceptionUtils;
import ua.belozorov.lunchvoting.util.hibernate.PhonesToStringConverter;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.util.*;

/**
 *
 * Created on 15.11.16.
 */
@Entity
@Table(name = "places")
@Getter
@DynamicUpdate
public class LunchPlace extends AbstractPersistableObject implements Comparable<LunchPlace> {

    @Column(name = "name", nullable = false)
    private final String name;

    @Column(name = "address")
    private final String address;

    @Column(name = "description")
    private final String description;

    @Column(name = "phones")
    @OrderBy
    @Convert(converter = PhonesToStringConverter.class)
    @SuppressWarnings("JpaAttributeTypeInspection")
    private final Set<String> phones;

    @OneToMany(mappedBy = "lunchPlace")
    private final Set<Menu> menus;

    protected LunchPlace() {
        this.phones = Collections.emptySet();
        this.menus = Collections.emptySet();
        this.description = null;
        this.address = null;
        this.name = null;
    }

    public LunchPlace(String name) {
        this(null, name, null, null, Collections.emptySet());
    }

    public LunchPlace(@Nullable String id, String name, String address, String description,
                      Set<String> phones) {
        this(id, null, name, address, description, phones, Collections.emptySet());
    }

    @Builder(toBuilder = true)
    LunchPlace(@Nullable String id, @Nullable Integer version, String name, String address, String description,
                      Set<String> phones, Set<Menu> menus) {
        super(id, version);
        ExceptionUtils.checkParamsNotNull(name, phones, menus);

        this.name = name;
        this.address = address == null ? "" : address;
        this.description = description == null ? "" : description;
        this.phones = phones;
        this.menus = menus;
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

