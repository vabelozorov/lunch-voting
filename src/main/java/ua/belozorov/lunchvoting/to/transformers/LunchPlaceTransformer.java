package ua.belozorov.lunchvoting.to.transformers;

import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.lunchplace.Menu;
import ua.belozorov.lunchvoting.to.LunchPlaceTo;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 21.11.16.
 */
public class LunchPlaceTransformer {

    public static LunchPlace toEntity(LunchPlaceTo to, String adminId) {
        Objects.requireNonNull(to, "DTO must not be null");
        Objects.requireNonNull(adminId, "AdminID must not be null");

        return new LunchPlace(to.getId(), to.getName(), to.getAddress(),
                to.getDescription(), to.getPhones(), Collections.emptyList(), adminId);
    }

    public static LunchPlaceTo toDto(LunchPlace entity) {
        Objects.requireNonNull(entity, "Entity for transforming into DTO must not be null");

        return new LunchPlaceTo(entity.getId(), entity.getName(), entity.getAddress(), entity.getDescription(),
                entity.getPhones());
    }

    public static Collection<LunchPlaceTo> collectionToDto(Collection<LunchPlace> places) {
        Objects.requireNonNull(places, "Collection of Entities for transforming into DTO must not be null");

        return places.stream()
                .map(LunchPlaceTransformer::toDto)
                .collect(Collectors.toList());
    }
}
