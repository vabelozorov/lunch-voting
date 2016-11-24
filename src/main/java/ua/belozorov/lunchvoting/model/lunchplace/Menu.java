package ua.belozorov.lunchvoting.model.lunchplace;

import lombok.Getter;
import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 21.11.16.
 */
@Entity
@Table(name = "menus")
@Getter
public class Menu extends AbstractPersistableObject {

    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate = LocalDate.now();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "dishes", joinColumns = @JoinColumn(name = "menu_id"))
    private List<Dish> dishes = new ArrayList<>();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private LunchPlace lunchPlace;
}
