package ua.belozorov.lunchvoting.to;

import lombok.Getter;
import lombok.Setter;
import lombok.Singular;
import lombok.ToString;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.lunchplace.Menu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 21.11.16.
 */
@Getter
@Setter
@ToString(doNotUseGetters = true)
public class LunchPlaceTo {
    private String id;
    private String name;
    private String address;
    private String description;
    private Collection<String> phones = new ArrayList<>();
    private Collection<Menu> menu = new ArrayList<>();
    private Menu todayMenu;

    public LunchPlaceTo() {
    }

    public LunchPlaceTo(String id, String name, String address, String description, Collection<String> phones) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.description = description;
        this.phones = Objects.requireNonNull(phones);
    }
}
