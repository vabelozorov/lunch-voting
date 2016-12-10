package ua.belozorov.lunchvoting.to;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.belozorov.lunchvoting.model.lunchplace.Dish;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.lunchplace.Menu;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 26.11.16.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuTo {
    private String id;
    private LocalDate effectiveDate;
    private List<Dish> dishes;
    private String lunchPlaceId;
}
