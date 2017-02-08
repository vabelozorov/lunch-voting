package ua.belozorov.lunchvoting.to;

import lombok.*;
import org.hibernate.validator.constraints.NotBlank;
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
@ToString(doNotUseGetters = true)
public final class LunchPlaceTo {

    private final String id;

    @NotBlank
    private final String name;
    private final String address;
    private final String description;
    private final Collection<String> phones;
    private final Collection<MenuTo> menus;

    protected LunchPlaceTo() {
        id = null;
        name = null;
        address = null;
        description = null;
        phones = null;
        menus = null;
    }

    public LunchPlaceTo(String id, String name, String address, String description, Collection<String> phones) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.description = description;
        this.phones = Objects.requireNonNull(phones);
        this.menus = Collections.emptyList();
    }

    @Builder(toBuilder = true)
    public LunchPlaceTo(String id, String name, String address, String description, Collection<String> phones, Collection<MenuTo> menus) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.description = description;
        this.phones = phones;
        this.menus = menus;
    }
}
