package ua.belozorov.lunchvoting.model;

import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 20.11.16.
 */
@Entity
@Table(name = "votes")
public class Vote extends AbstractPersistableObject {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User voter;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private LunchPlace target;

    @Column(name = "voteTime")
    private LocalDateTime voteTime = LocalDateTime.now();
}
