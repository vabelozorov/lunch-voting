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
public final class Dish implements Comparable<Dish> {
    private String name;
    private float price;
    private int position;

    protected Dish() {
    }

    public Dish(String name, float price, int position) {
        this.name = name;
        this.price = price;
    }

    @Override
    public int compareTo(final Dish o) {
        return Integer.compare(this.position, o.position);
    }
}
