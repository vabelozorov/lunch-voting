package ua.belozorov.lunchvoting.to;

import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;
import ua.belozorov.lunchvoting.model.voting.polling.Poll;
import ua.belozorov.lunchvoting.model.voting.polling.PollItem;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 31.01.17.
 */
public final class PollTo {
    private final String id;
    private final List<String> pollItemIds;
    private final LocalDate menuDate;
    private final List<PollItem> pollItems;

    public PollTo(Poll poll, boolean onlyItemIds) {
        this.id = ((AbstractPersistableObject)poll).getId();
        if (onlyItemIds) {
            this.pollItemIds = poll.getPollItems().stream()
                    .map(PollItem::getId)
                    .collect(Collectors.toList());
            this.pollItems = null;
        } else {
          this.pollItems = poll.getPollItems();
          this.pollItemIds = null;
        }
        this.menuDate = poll.getMenuDate();
    }
}
