package ua.belozorov.lunchvoting.to.transformers;

import org.jetbrains.annotations.NotNull;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
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
public class DtoIntoEntity {

    public static LunchPlace toLunchPlace(LunchPlaceTo to, String adminId) {
        return new LunchPlace(to.getId(), to.getName(), to.getAddress(),
                to.getDescription(), to.getPhones(), Collections.emptyList(), adminId);
    }
}
