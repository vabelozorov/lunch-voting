package ua.belozorov.lunchvoting.model.lunchplace;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;
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

import static ua.belozorov.lunchvoting.util.ExceptionUtils.NOT_CHECK;

/**
 * <p>Immutable class that represent a place where a voter can have a meal.</p>
 * Contains menus which in turn contain dishes.
 * <p>This class implements {@link Comparable} to support a natural sorting which is by its {@code name},
 * As a uniqueness of the name is enforced only within an {@link EatingArea}, care should be taken when putting
 * the instances of the class from different {@code EatingArea}s to Java collections.</p>
 * <p><b>NOTE:</b>
 * As Hibernate cannot create a proxy for {@code One-To-One} and {@code Many-To-One} final classes,
 * {@code final} modifier here is omitted, yet inheritance of this class in not desired.</p>
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

    /**
     * JPA
     */
    LunchPlace() {
        this.phones = Collections.emptySet();
        this.menus = Collections.emptySet();
        this.description = null;
        this.address = null;
        this.name = null;
    }

    /**
     * Simple constructor that accepts one non-null parameter.
     * ID is auto-generated
     * Other values are set to an empty string or an empty collection, depending on a field type
     *
     * @param name LunchPlace name, must be unique in its area
     */
    public LunchPlace(String name) {
        this(null, name, null, null, Collections.emptySet());
    }

    /**
     *
     * @param id any string or null to auto-generate
     * @param name LunchPlace name, must be unique in its area
     * @param address any string or null for setting an empty string
     * @param description any string or null for setting an empty string
     * @param phones
     */
    public LunchPlace(@Nullable String id, String name, @Nullable String address, @Nullable String description,
                      Set<String> phones) {
        this(id, null, name, address, description, phones, Collections.emptySet());
    }

    /**
     * All-args constructor for cloning setters
     * @param id any string or null to auto-generate
     * @param version a positive value to indicate a persisted instance or null for a transient instance
     * @param name LunchPlace name, must be unique in its area
     * @param address
     * @param description
     * @param phones
     * @param menus
     */
    private LunchPlace(@Nullable String id, @Nullable Integer version, String name, @Nullable String address, @Nullable String description,
                      Set<String> phones, Set<Menu> menus) {
        super(id, version);

        ExceptionUtils.checkParamsNotNull(NOT_CHECK, NOT_CHECK, name, NOT_CHECK, NOT_CHECK, phones, menus);

        this.name = name;
        this.address = address == null ? "" : address;
        this.description = description == null ? "" : description;
        this.phones = phones;
        this.menus = menus;
    }

    /**
     * @return set of strings for phones sorted in the natural order
     */
    public Set<String> getPhones() {
        return ImmutableSortedSet.copyOf(this.phones);
    }

    /**
     * @return Menu objects sorted according to string natural sorting
     */
    public Set<Menu> getMenus() {
        return ImmutableSortedSet.copyOf(this.menus);
    }

    public LunchPlace withName(String name) {
        return new LunchPlace(id, version, name, address, description, phones, menus);
    }

    public LunchPlace withAddress(String address) {
        return new LunchPlace(id, version, name, address, description, phones, menus);
    }

    public LunchPlace withDescription(String description) {
        return new LunchPlace(id, version, name, address, description, phones, menus);
    }

    public LunchPlace withPhones(Set<String> phones) {
        return new LunchPlace(id, version, name, address, description, Sets.newHashSet(phones), menus);
    }

    public LunchPlace withMenus(Set<Menu> menus) {
        return new LunchPlace(id, version, name, address, description, phones, Sets.newHashSet(menus));
    }

    @Override
    public int compareTo(LunchPlace o) {
        return this.name.compareTo(o.name);
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
}

