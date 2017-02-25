package ua.belozorov.lunchvoting.to;

import lombok.Getter;
import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;
import ua.belozorov.lunchvoting.model.voting.polling.PollItem;
import ua.belozorov.lunchvoting.model.voting.polling.TimeConstraint;

import java.time.LocalDate;
import java.util.List;

/**
 *
 * Created on 31.01.17.
 */
@Getter
public final class PollTo {
    private final String id;
    private final LocalDate menuDate;
    private final TimeConstraint timeConstraint;
    private final List<PollItem> pollItems;

    public PollTo(LunchPlacePoll poll) {
        this(poll, true);
    }

    public PollTo(LunchPlacePoll poll, boolean includePollItems) {
        this.id = poll.getId();
        this.pollItems = includePollItems ? poll.getPollItems() : null;
        this.menuDate = poll.getMenuDate();
        this.timeConstraint = poll.getTimeConstraint();
    }
}
