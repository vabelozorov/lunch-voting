package ua.belozorov.lunchvoting.model.lunchplace;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.NotBlank;
import ua.belozorov.lunchvoting.util.ExceptionUtils;

import javax.annotation.PostConstruct;
import javax.persistence.Embeddable;
import javax.persistence.PostLoad;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Immutable Dish for {@link Menu}
 * <p>This class implements {@link Comparable} to support a natural sorting which is by its {@code position}</p>
 * Created on 15.11.16.
 */
@Embeddable
@Getter
public final class Dish implements Comparable<Dish> {

    private final String name;

    private final float price;

    private final int position;

    /**
     * JPA
     */
    Dish() {
        name = "";
        price = -1f;
        position = -1;
    }

    /**
     * @param name
     * @param price >=0
     * @param position must be unique within the same menu and >=0
     */
    public Dish(String name, float price, int position) {
        ExceptionUtils.checkParamsNotNull(name);

        if (price < 0 || position < 0) {
            throw new IllegalStateException("price or position cannot be less than 0");
        }

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
