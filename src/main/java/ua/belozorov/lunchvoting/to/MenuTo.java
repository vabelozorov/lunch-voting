package ua.belozorov.lunchvoting.to;

import com.google.common.collect.ImmutableSortedSet;
import lombok.*;
import ua.belozorov.lunchvoting.model.lunchplace.Dish;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.lunchplace.Menu;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 *
 * Created on 26.11.16.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public final class MenuTo {

    private final String id;

    @NotNull
    private final LocalDate effectiveDate;

    @NotNull
    private final Set<Dish> dishes;

    private final String lunchPlaceId;

    public MenuTo(LocalDate effectiveDate, Set<Dish> dishes) {
        this(null, effectiveDate, dishes, null);
    }

    public MenuTo(String id, LocalDate effectiveDate, Set<Dish> dishes, String lunchPlaceId) {
        this.id = id;
        this.effectiveDate = effectiveDate;
        this.dishes = ImmutableSortedSet.copyOf(dishes);
        this.lunchPlaceId = lunchPlaceId;
    }
}
