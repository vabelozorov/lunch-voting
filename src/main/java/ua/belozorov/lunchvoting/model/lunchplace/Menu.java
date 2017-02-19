package ua.belozorov.lunchvoting.model.lunchplace;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSortedSet;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;
import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.*;

import static java.util.Optional.ofNullable;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 21.11.16.
 */
@Entity
@Table(name = "menus")
@Getter
@DynamicUpdate
public final class Menu extends AbstractPersistableObject implements Comparable<Menu> {

    @Column(name = "effective_date", nullable = false)
    @NotNull
    private final LocalDate effectiveDate;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "dishes", joinColumns = @JoinColumn(name = "menu_id"))
    @NotEmpty
    private final Set<Dish> dishes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    @NotNull
    @JsonIgnore
    private final LunchPlace lunchPlace;

    /**
     *  For JPA only
     */
    protected Menu() {
        super(null, null);
        effectiveDate = null;
        dishes = null;
        lunchPlace = null;
    }

    @Builder
    public Menu(LocalDate effectiveDate, Set<Dish> dishes, LunchPlace lunchPlace) {
        this(null, null, effectiveDate, dishes, lunchPlace);
    }

    /**
     * All-args constructor. Intended for using in tests
     *
     * @param id should be null when creating a new entity, non-empty/non-blank string otherwise
     * @param version must be null for a new entity
     * @param effectiveDate - if null, the current time is used
     * @param dishes must not be empty collection or null. Empty menu does not make much sense
     * @param lunchPlace
     */
    private Menu(String id, Integer version, LocalDate effectiveDate, Collection<Dish> dishes, LunchPlace lunchPlace) {
        super(id, version);
        this.effectiveDate = Objects.requireNonNull(effectiveDate, "effectiveDate must not be null");
        this.dishes = Objects.requireNonNull(new HashSet<>(dishes));
        this.lunchPlace = Objects.requireNonNull(lunchPlace);
    }

    public Set<Dish> getDishes() {
        return ImmutableSortedSet.copyOf(this.dishes);
    }

    public static MenuBuilder builder() {
        return new MenuBuilder();
    }

    public static MenuBuilder builder(Menu menu) {
        return new MenuBuilder(menu);
    }

    @Override
    public int compareTo(Menu o) {
        int i = (-1) * this.effectiveDate.compareTo(o.effectiveDate);
        return i != 0 ? i : this.id.compareTo(o.id);
    }

    public static class MenuBuilder {
        private LocalDate effectiveDate;
        private Collection<Dish> dishes;
        private LunchPlace lunchPlace;
        private Integer version;
        private String id;

        public MenuBuilder() { }

        public MenuBuilder(Menu menu) {
            this.id = menu.id;
            this.version = menu.version;
            this.effectiveDate = menu.effectiveDate;
            this.dishes = menu.dishes;
            this.lunchPlace = menu.lunchPlace;
        }

        public Menu build() {
            return new Menu(id, version, effectiveDate, dishes, lunchPlace);
        }
    }
}
