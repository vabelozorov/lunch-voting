package ua.belozorov.lunchvoting.model.lunchplace;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import javax.persistence.Embeddable;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 15.11.16.
 */
@Embeddable
@Getter
@Immutable
public final class Dish implements Comparable<Dish> {
    private final String name;
    private final float price;
    private final int position;

    protected Dish() {
        name = null;
        price = 0;
        position = 0;
    }

    public Dish(String name, float price, int position) {
        this.name = name;
        this.price = price;
        this.position = position;
    }

    @Override
    public int compareTo(final Dish o) {
        return Integer.compare(this.position, o.position);
    }
}
