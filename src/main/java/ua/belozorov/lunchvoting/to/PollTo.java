package ua.belozorov.lunchvoting.to;

import lombok.Getter;
import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;
import ua.belozorov.lunchvoting.model.base.Persistable;
import ua.belozorov.lunchvoting.model.voting.polling.Poll;
import ua.belozorov.lunchvoting.model.voting.polling.PollItem;

import java.time.LocalDate;
import java.util.List;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 31.01.17.
 */
@Getter
public final class PollTo {
    private final String id;
    private final LocalDate menuDate;
    private final List<PollItem> pollItems;

    public PollTo(Poll poll) {
        this.id = poll.getId();
        this.pollItems = poll.getPollItems();
        this.menuDate = poll.getMenuDate();
    }
}
