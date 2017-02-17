package ua.belozorov.lunchvoting.to;

import com.google.common.collect.ImmutableSortedSet;
import lombok.*;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.SafeHtml;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.lunchplace.Menu;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 21.11.16.
 */
@Getter
@ToString(doNotUseGetters = true)
public final class LunchPlaceTo {

    @NotBlank(groups = Create.class)
    @SafeHtml
    @Size(max=50)
    private final String name;

    @SafeHtml
    @Size(max=100)
    private final String address;

    @SafeHtml
    @Size(max=1000)
    private final String description;
    private final Set<String> phones;

    protected LunchPlaceTo() {
        name = null;
        address = null;
        description = null;
        phones = null;
    }

    @Builder(toBuilder = true)
    public LunchPlaceTo(String name, String address, String description, Set<String> phones) {
        this.name = name;
        this.address = address;
        this.description = description;
        this.phones = ImmutableSortedSet.copyOf(phones);
    }

    public interface Create {}
}
