package ua.belozorov.lunchvoting.to;

import ua.belozorov.lunchvoting.model.lunchplace.Menu;

import java.util.ArrayList;
import java.util.Collection;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 21.11.16.
 */
public class LunchPlaceTo {
    private String id;
    private String name;
    private String address;
    private String description;
    private Collection<String> phones = new ArrayList<>();
    private Menu todayMenu;

    public LunchPlaceTo() {
    }

    public LunchPlaceTo(String id, String name, String address, String description, Collection<String> phones, Menu todayMenu) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.description = description;
        this.phones = phones;
        this.todayMenu = todayMenu;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPhones(Collection<String> phones) {
        this.phones = phones;
    }

    public void setTodayMenu(Menu todayMenu) {
        this.todayMenu = todayMenu;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public Collection<String> getPhones() {
        return phones;
    }

    public Menu getTodayMenu() {
        return todayMenu;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
