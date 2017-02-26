package ua.belozorov.lunchvoting.model.lunchplace;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSortedSet;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.NotEmpty;
import org.jetbrains.annotations.Nullable;
import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;
import ua.belozorov.lunchvoting.util.ExceptionUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Optional.ofNullable;
import static ua.belozorov.lunchvoting.util.ExceptionUtils.NOT_CHECK;

/**
 * Immutable class that represents menu of a certain {@link LunchPlace}
 *
 * Created on 21.11.16.
 */
@Entity
@Table(name = "menus")
@Getter
@DynamicUpdate
public final class Menu extends AbstractPersistableObject implements Comparable<Menu> {

    @Column(name = "effective_date", nullable = false)
    private final LocalDate effectiveDate;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "dishes", joinColumns = @JoinColumn(name = "menu_id"))
    private final Set<Dish> dishes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    @JsonIgnore
    private final LunchPlace lunchPlace;

    /**
     *  JPA
     */
    Menu() {
        effectiveDate = null;
        dishes = Collections.emptySet();
        lunchPlace = null;
    }

    /**
     * Simple constructor with auto-generating for ID
     * @param effectiveDate a date for which this menu will be considered valid
     * @param dishes
     * @param lunchPlace
     */
    public Menu(LocalDate effectiveDate, Set<Dish> dishes, LunchPlace lunchPlace) {
        this(null, null, effectiveDate, dishes, lunchPlace);
    }

    /**
     * All-args constructor for cloning setters
     * @param id any string or null to auto-generate
     * @param version a positive value to indicate a persisted instance or null for a transient instance
     * @param effectiveDate a date for which this menu will be considered valid
     * @param dishes
     * @param lunchPlace
     */
    private Menu(@Nullable String id, @Nullable Integer version, LocalDate effectiveDate, Set<Dish> dishes, LunchPlace lunchPlace) {
        super(id, version);

        ExceptionUtils.checkParamsNotNull(NOT_CHECK, NOT_CHECK, effectiveDate, dishes, lunchPlace);

        this.effectiveDate = effectiveDate;
        this.dishes = dishes;
        this.lunchPlace = lunchPlace;
    }

    public Set<Dish> getDishes() {
        return ImmutableSortedSet.copyOf(this.dishes);
    }

    public Menu withEffectiveDate(LocalDate effectiveDate) {
        return new Menu(id, version, effectiveDate, dishes, lunchPlace);
    }

    public Menu withDishes(Set<Dish> dishes) {
        return new Menu(id, version, effectiveDate, dishes, lunchPlace);
    }

    @Override
    public int compareTo(Menu o) {
        int i = (-1) * this.effectiveDate.compareTo(o.effectiveDate);
        return i != 0 ? i : this.id.compareTo(o.id);
    }
}
