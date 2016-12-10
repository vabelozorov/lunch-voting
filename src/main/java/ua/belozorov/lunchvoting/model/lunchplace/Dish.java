package ua.belozorov.lunchvoting.model.lunchplace;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 15.11.16.
 */
@Embeddable
@Getter
@Setter
public class Dish {
    private String name;
    private float price;

    protected Dish() {
    }

    public Dish(String name, float price) {
        this.name = name;
        this.price = price;
    }
}
