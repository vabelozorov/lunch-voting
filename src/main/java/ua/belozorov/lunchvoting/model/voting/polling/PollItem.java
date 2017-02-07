package ua.belozorov.lunchvoting.model.voting.polling;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;
import ua.belozorov.lunchvoting.model.base.Persistable;

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
    @Getter(AccessLevel.NONE)
    private int position;

    @Getter
    @Column(name = "item_id")
    private final String itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id")
    @NotNull
    @JsonIgnore
    private final LunchPlacePoll poll;

    protected PollItem() {
        itemId = null;
        poll = null;
    }

    public PollItem(Persistable item, LunchPlacePoll poll) {
        Objects.requireNonNull(item, "Error during initializing PollItem. Itemid must not be null");
        this.itemId = item.getId();
        this.poll = Objects.requireNonNull(poll, "Error during initializing PollItem. Poll must not be null");
    }
}
