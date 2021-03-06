package ua.belozorov.lunchvoting.to.transformers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.to.LunchPlaceTo;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

/**

 *
 * Created on 21.11.16.
 */
public class DtoIntoEntity {

    public static LunchPlace toLunchPlace(LunchPlaceTo to, @Nullable String id) {
        return new LunchPlace(id, to.getName(), to.getAddress(),
                to.getDescription(), to.getPhones());
    }
}
