package ua.belozorov.lunchvoting.model.lunchplace;

import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
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
public class Menu extends AbstractPersistableObject {

    @Column(name = "effective_date", nullable = false)
    @NotNull
    private final LocalDate effectiveDate;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "dishes", joinColumns = @JoinColumn(name = "menu_id"))
    @NotEmpty
    @OrderBy("position")
    private final SortedSet<Dish> dishes;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    @NotNull
    private LunchPlace lunchPlace;

    /**
     *  For JPA only
     */
    protected Menu() {
        super(null, null);
        effectiveDate = null;
        dishes = null;
        lunchPlace = null;
    }

    Menu(LocalDate effectiveDate, List<Dish> dishes, LunchPlace lunchPlace) {
        this(null, null, effectiveDate, dishes, lunchPlace);
    }

    @Builder
    Menu(String id, LocalDate effectiveDate, List<Dish> dishes, LunchPlace lunchPlace) {
        this(id, null, effectiveDate, dishes, lunchPlace);
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
        this.effectiveDate = effectiveDate == null ? LocalDate.now() : effectiveDate;
        this.dishes = Objects.requireNonNull(new TreeSet<>(dishes));
        this.lunchPlace = Objects.requireNonNull(lunchPlace);
    }

//
//    Menu setDishes(List<Dish> dishes) {
//        return builder(this).dishes(dishes).build();
//    }

    public static MenuBuilder builder() {
        return new MenuBuilder();
    }

    public static MenuBuilder builder(Menu menu) {
        return new MenuBuilder(menu);
    }

    public static class MenuBuilder {
        private LocalDate effectiveDate;
        private SortedSet<Dish> dishes;
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
