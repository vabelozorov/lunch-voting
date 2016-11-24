package ua.belozorov.lunchvoting.to.transformers;

import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.lunchplace.Menu;
import ua.belozorov.lunchvoting.to.LunchPlaceTo;

import java.util.Collections;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 21.11.16.
 */
public class LunchPlaceTransformer {

    public static LunchPlace toEntity(LunchPlaceTo to, User admin) {
        return new LunchPlace(to.getId(), to.getName(), to.getAddress(),
                to.getDescription(), to.getPhones(), Collections.emptyList(), admin);
    }

    public static LunchPlaceTo toDto(LunchPlace entity, Menu menu) {
        return new LunchPlaceTo(entity.getId(), entity.getName(), entity.getAddress(), entity.getDescription(),
                entity.getPhones(), menu);
    }
}
