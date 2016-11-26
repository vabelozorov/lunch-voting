package ua.belozorov.lunchvoting.to.transformers;

import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.lunchplace.Menu;
import ua.belozorov.lunchvoting.to.LunchPlaceTo;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 21.11.16.
 */
public class LunchPlaceTransformer {

    public static LunchPlace toEntity(LunchPlaceTo to) {
        return new LunchPlace(to.getId(), to.getName(), to.getAddress(),
                to.getDescription(), to.getPhones(), Collections.emptyList());
    }

    public static LunchPlaceTo toDto(LunchPlace entity) {
        return new LunchPlaceTo(entity.getId(), entity.getName(), entity.getAddress(), entity.getDescription(),
                entity.getPhones());
    }

    public static Collection<LunchPlaceTo> collectionToDto(Collection<LunchPlace> places) {
        return places.stream()
                .map(LunchPlaceTransformer::toDto)
                .collect(Collectors.toList());
    }
}
