package ua.belozorov.lunchvoting.model.voting.polling;

import lombok.AccessLevel;
import lombok.Getter;
import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 03.12.16.
 */
@Getter(AccessLevel.PACKAGE)
@Entity
@Table(name = "poll_items")
public final class PollItem extends AbstractPersistableObject {

    @Column(name = "position")
    private int position;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    @Getter
    private final LunchPlace item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id")
    @NotNull
    private final LunchPlacePoll poll;

    protected PollItem() {
//        position = 0;
        item = null;
        poll = null;
    }

    public PollItem(LunchPlace item, LunchPlacePoll poll) {
        this.item = Objects.requireNonNull(item, "Error during initializing PollItem. LunchPlace must not be null");
        this.poll = Objects.requireNonNull(poll, "Error during initializing PollItem. Poll must not be null");
    }

//    public PollItem(int position, LunchPlace item, LunchPlacePoll poll) {
//        this.position = position;
//        this.item = Objects.requireNonNull(item, "Error during initializing PollItem. LunchPlace must not be null");
//        this.poll = Objects.requireNonNull(poll, "Error during initializing PollItem. Poll must not be null");
//    }
}
