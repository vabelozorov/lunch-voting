package ua.belozorov.lunchvoting.model.lunchplace;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.NotBlank;

import javax.annotation.PostConstruct;
import javax.persistence.Embeddable;
import javax.persistence.PostLoad;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 15.11.16.
 */
@Embeddable
@Getter
@Immutable
public final class Dish implements Comparable<Dish> {

    @NotBlank
    private final String name;

    private final float price;

    private final int position;

    protected Dish() {
        name = "";
        price = -1f;
        position = -1;
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

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Dish)) return false;

        Dish dish = (Dish) object;

        if (Float.compare(dish.price, price) != 0) return false;
        if (position != dish.position) return false;
        return name.equals(dish.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (price != +0.0f ? Float.floatToIntBits(price) : 0);
        result = 31 * result + position;
        return result;
    }
}
