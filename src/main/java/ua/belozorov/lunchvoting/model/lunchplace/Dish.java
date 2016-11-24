package ua.belozorov.lunchvoting.model.lunchplace;

import javax.persistence.Embeddable;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 15.11.16.
 */
@Embeddable
public class Dish {
    private String name;
    private float price;
}
